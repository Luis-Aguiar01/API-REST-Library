package com.luis.aguiar.services;

import com.luis.aguiar.dto.LoanRequestDto;
import com.luis.aguiar.dto.LoanResponseDto;
import com.luis.aguiar.enums.*;
import com.luis.aguiar.exceptions.BookNotAvailableException;
import com.luis.aguiar.exceptions.EntityNotFoundException;
import com.luis.aguiar.exceptions.LoanNotAvailableException;
import com.luis.aguiar.models.*;
import com.luis.aguiar.repositories.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.*;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private LoanService service;
    private User user;
    private Book book;
    private Loan loan;
    private Loan loan2;
    private LoanRequestDto requestDto;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(UUID.randomUUID())
                .firstName("Luis")
                .lastName("Aguiar")
                .email("luis@gmail.com")
                .password("e10adc3949ba59abbe56e057f20f883e")
                .birthDate(LocalDate.of(2000, 10, 10))
                .hasBookOnLoan(false)
                .role(Role.USER)
                .loans(new HashSet<>())
                .build();
        book = new Book(
                UUID.randomUUID(),
                "O Senhor dos Anéis",
                new HashSet<>(),
                new HashSet<>(),
                LocalDate.of(1920, 10, 10),
                Status.AVAILABLE
        );
        loan = Loan.builder()
                .id(UUID.randomUUID())
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(7))
                .active(false)
                .build();
        loan2 = Loan.builder()
                .id(UUID.randomUUID())
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(7))
                .active(true)
                .build();
        requestDto = new LoanRequestDto(
                UUID.randomUUID(),
                "luis@gmail.com"
        );
    }

    @Test
    @DisplayName(
            "Should return a LoanResponseDto " +
            "when a valid LoanRequestDto is passed as an argument to saveLoan method."
    )
    void shouldReturnALoanResponseDto_whenAValidLoanRequestDtoIsPassedAsAnArgumentToSaveLoanMethod() {
        // given
        given(bookRepository.findById(any(UUID.class))).willReturn(Optional.of(book));
        given(userRepository.findByEmail(any(String.class))).willReturn(Optional.of(user));
        given(loanRepository.save(any(Loan.class))).willReturn(loan);

        // when
        LoanResponseDto loanDto = service.saveLoan(requestDto);

        // then
        assertThat(loanDto).isNotNull();
        assertThat(loanDto.getBook().getId()).isEqualTo(book.getId());
        assertThat(loanDto.getUser().getEmail()).isEqualTo(user.getEmail());
        assertThat(loanDto.getLoanDate()).isEqualTo(loan.getLoanDate());
        assertThat(loanDto.getReturnDate()).isEqualTo(loan.getReturnDate());

        // verify
        then(bookRepository).should(times(1)).findById(any(UUID.class));
        then(bookRepository).shouldHaveNoMoreInteractions();
        then(userRepository).should(times(1)).findByEmail(any(String.class));
        then(userRepository).shouldHaveNoMoreInteractions();
        then(loanRepository).should(times(1)).save(any(Loan.class));
        then(loanRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should throw an exception " +
            "when a non-existing Book ID is passed as an argument to saveLoan method."
    )
    void shouldThrowAnException_whenANonExistingBookIdIsPassedAsAnArgumentToSaveLoanMethod() {
        // given
        given(bookRepository.findById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("No books with this ID could be found."));

        // when
        assertThatThrownBy(() -> service.saveLoan(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No books with this ID could be found.");

        then(bookRepository).should(times(1)).findById(any(UUID.class));
        then(bookRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an exception when the Book is unavailable to saveLoan method.")
    void shouldThrowAnException_whenTheBookIsUnavailableToSaveLoanMethod() {
        // given
        book.setStatus(Status.UNAVAILABLE);
        given(bookRepository.findById(any(UUID.class))).willReturn(Optional.of(book));

        // when
        assertThatThrownBy(() -> service.saveLoan(requestDto))
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessage("The book is not available.");

        // verify
        then(bookRepository).should(times(1)).findById(any(UUID.class));
        then(bookRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an exception when a non-existing email is passed as an argument to saveLoan methdo.")
    void shouldThrowAnException_whenANonExistingEmailIsPassedAsAnArgumentToSaveLoanMethod() {
        // given
        given(bookRepository.findById(any(UUID.class))).willReturn(Optional.of(book));
        given(userRepository.findByEmail(any(String.class)))
                .willThrow(new EntityNotFoundException("No user with this ID could be found."));

        // when & then
        assertThatThrownBy(() -> service.saveLoan(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No user with this ID could be found.");

        // verify
        then(bookRepository).should(times(1)).findById(any(UUID.class));
        then(bookRepository).shouldHaveNoMoreInteractions();
        then(userRepository).should(times(1)).findByEmail(any(String.class));
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an exception when the user already has an active loan to saveLoan method.")
    void shouldThrowAnException_whenTheUserAlreadyHasAnActiveLoanToSaveLoanMethod() {
        // given
        user.setHasBookOnLoan(true);
        given(bookRepository.findById(any(UUID.class))).willReturn(Optional.of(book));
        given(userRepository.findByEmail(any(String.class))).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> service.saveLoan(requestDto))
                .isInstanceOf(LoanNotAvailableException.class)
                .hasMessage("You already have an active loan.");

        then(bookRepository).should(times(1)).findById(any(UUID.class));
        then(bookRepository).shouldHaveNoMoreInteractions();
        then(userRepository).should(times(1)).findByEmail(any(String.class));
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should return a Loan when an existing loan ID is passed as an argument to findById method.")
    void shouldReturnALoan_whenAnExistingLoanIdIsPassedAsAnArgumentToFindByIdMethod() {
        // given
        given(loanRepository.findById(any(UUID.class))).willReturn(Optional.of(loan));

        // when
        LoanResponseDto loanDto = service.findById(UUID.randomUUID());

        // then
        assertThat(loanDto.getId()).isEqualByComparingTo(loan.getId());
        assertThat(loanDto.getUser().getEmail()).isEqualTo(loan.getUser().getEmail());
        assertThat(loanDto.getUser().getLastName()).isEqualTo(loan.getUser().getLastName());
        assertThat(loanDto.getUser().getFirstName()).isEqualTo(loan.getUser().getFirstName());
        assertThat(loanDto.getBook().getTitle()).isEqualTo(loan.getBook().getTitle());

        // verify
        then(loanRepository).should(times(1)).findById(any(UUID.class));
        then(loanRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an exception when a non-existing ID is passed as an argument to findById method.")
    void shouldThrowAnException_whenANonExistingIdIsPassedAsAnArgumentToFindByIdMethod() {
        // given
        given(loanRepository.findById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("No loan with this ID could be found."));

        // when & then
        assertThatThrownBy(() -> service.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No loan with this ID could be found.");

        then(loanRepository).should(times(1)).findById(any(UUID.class));
        then(loanRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should return a list of loan when a boolean is passed as an argument to findByStatus method.")
    void shouldReturnAListOfLoan_whenABooleanIsPassedAsAnArgumentToFindByStatusMethod() {
        // given
        given(loanRepository.findByActive(any(Boolean.class), any(PageRequest.class)))
                .willReturn(List.of(loan));

        // when
        List<LoanResponseDto> loansDto = service.findByStatus(true, 0, 2);

        // then
        assertThat(loansDto).isNotNull();
        assertThat(loansDto).isNotEmpty();
        assertThat(loansDto.get(0)).isInstanceOf(LoanResponseDto.class);
        assertThat(loansDto.get(0).getId()).isEqualTo(loan.getId());

        // verify
        then(loanRepository).should(times(1))
                .findByActive(any(Boolean.class), any(PageRequest.class));
        then(loanRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should return a list of loans by User email " +
            "when an User email is passed as an argument to findByUserEmail method."
    )
    void shouldReturnAListOfLoansByUserEmail_whenAnUserEmailIsPassedAsAnArgumentToFindByUserEmailMethod() {
        // given
        given(loanRepository.findByUserEmail(any(String.class), any(PageRequest.class)))
                .willReturn(List.of(loan, loan2));

        // when
        List<LoanResponseDto> loansDto = service.findByUserEmail("luis@gmail.com", 0, 2);
        
        // then
        assertThat(loansDto).isNotNull();
        assertThat(loansDto).isNotEmpty();
        assertThat(loansDto.get(0).getUser().getEmail()).isEqualTo(user.getEmail());
        assertThat(loansDto.get(1).getUser().getEmail()).isEqualTo(user.getEmail());
        assertThat(loansDto.size()).isEqualTo(2);

        then(loanRepository).should(times(1))
                .findByUserEmail(any(String.class), any(PageRequest.class));
        then(loanRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should return a list of loans " +
            "when an user email and a status is passed as an argument to findByUserAndActive method."
    )
    void shouldReturnAListOfLoans_whenAnUserEmailAndAStatusIsPassedAsAnArgumentToFindByUserAndActiveMethod() {
        // given
        given(loanRepository.findByUserEmailAndActive(any(String.class), any(Boolean.class), any(PageRequest.class)))
                .willReturn(List.of(loan));

        // when
        List<LoanResponseDto> loansDto = service
                .findByUserAndActive("luis@gmail.com", true, 0, 2);

        // then
        assertThat(loansDto).isNotNull();
        assertThat(loansDto).isNotEmpty();
        assertThat(loansDto.size()).isEqualTo(1);

        then(loanRepository).should(times(1))
                .findByUserEmailAndActive(any(String.class), any(Boolean.class), any(PageRequest.class));
        then(loanRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should successfully return loan when valid loan ID and email are provided to returnLoan method.")
    void shouldSuccessfullyReturnLoan_whenValidLoanIdAndEmailAreProvided() {
        // given
        given(loanRepository.findByIdAndUserEmail(any(UUID.class), any(String.class)))
                .willReturn(Optional.of(loan2));

        // when
        service.returnLoan(UUID.randomUUID(), "luis@gmail.com");

        // then
        then(loanRepository).should(times(1))
                .findByIdAndUserEmail(any(UUID.class), any(String.class));
        then(loanRepository).shouldHaveNoMoreInteractions();
        then(bookRepository).should(times(1)).save(any(Book.class));
        then(bookRepository).shouldHaveNoMoreInteractions();
        then(userRepository).should(times(1)).save(any(User.class));
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an exception when no loan with given ID and Email is found.")
    void shouldThrowAnException_whenNoLoanWithGivenIdAndEmailIsFound() {
        // given
        given(loanRepository.findByIdAndUserEmail(any(UUID.class), any(String.class)))
                .willThrow(new EntityNotFoundException("No loans with this ID can be found associated with this email."));

        // when & then
        assertThatThrownBy(() -> service.returnLoan(UUID.randomUUID(), "luis@gmail.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No loans with this ID can be found associated with this email.");

        then(loanRepository).should(times(1))
                .findByIdAndUserEmail(any(UUID.class), any(String.class));
        then(loanRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an exception when the loan has already been returned")
    void shouldThrowAnExceptionWhenTheLoanHasAlreadyBeenReturned() {
        // given
        given(loanRepository.findByIdAndUserEmail(any(UUID.class), any(String.class)))
                .willReturn(Optional.of(loan));

        // when & then
        assertThatThrownBy(() -> service.returnLoan(UUID.randomUUID(), "luis@gmail.com"))
                .isInstanceOf(LoanNotAvailableException.class)
                .hasMessage("The loan has already been returned.");

        then(loanRepository).should(times(1))
                .findByIdAndUserEmail(any(UUID.class), any(String.class));
        then(loanRepository).shouldHaveNoMoreInteractions();
    }
}