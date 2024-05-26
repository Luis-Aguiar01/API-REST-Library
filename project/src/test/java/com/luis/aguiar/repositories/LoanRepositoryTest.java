package com.luis.aguiar.repositories;

import com.luis.aguiar.enums.Role;
import com.luis.aguiar.enums.Status;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.models.Loan;
import com.luis.aguiar.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class LoanRepositoryTest {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    private Book book;
    private User user;
    private Loan loan;

    @BeforeEach
    void setup() {
        book = new Book(
                null,
                "O Senhor dos Anéis",
                new HashSet<>(),
                new HashSet<>(),
                LocalDate.of(1920, 10, 10),
                Status.AVAILABLE
        );
        user = new User(
                null,
                "Luis",
                "Aguiar",
                "luis@gmail.com",
                "123456",
                LocalDate.of(2000, 10,10),
                true, Role.USER,
                new HashSet<>()
        );
        loan = new Loan(
                null,
                book,
                user,
                LocalDate.of(2024, 10, 10),
                LocalDate.of(2024, 10, 17),
                true
        );
        userRepository.save(user);
        bookRepository.save(book);
        loanRepository.save(loan);
    }

    @Test
    @DisplayName("Should return a list of Loans when a status is passed as an argument to findByActive method.")
    void shouldReturnAListOfLoans_whenAStatusIsPassedAsAnArgumentToFindByActiveMethod() {
        var findLoans = loanRepository.findByActive(true, Pageable.unpaged());

        assertThat(findLoans).isNotNull();
        assertThat(findLoans.size()).isEqualTo(1);
        assertThat(findLoans.iterator().next().getUser().getEmail()).isEqualTo("luis@gmail.com");
        assertThat(findLoans.iterator().next().getBook().getTitle()).isEqualTo("O Senhor dos Anéis");

        findLoans = loanRepository.findByActive(false, Pageable.unpaged());

        assertThat(findLoans).isNotNull();
        assertThat(findLoans.size()).isEqualTo(0);
    }

    @Test
    @DisplayName(
            "Should return a list of Loans " +
            "when a user email is passed as an argument to findByUserEmail method."
    )
    void shouldReturnAListOfLoans_whenAUserEmailIsPassedAsAnArgumentToFindByUserEmailMethod() {
        var findLoans = loanRepository.findByUserEmail("luis@gmail.com", Pageable.unpaged());

        assertThat(findLoans).isNotNull();
        assertThat(findLoans.size()).isEqualTo(1);
        assertThat(findLoans.iterator().next().getBook().getTitle()).isEqualTo("O Senhor dos Anéis");

        findLoans = loanRepository.findByUserEmail("ana@gmail.com", Pageable.unpaged());

        assertThat(findLoans).isNotNull();
        assertThat(findLoans.size()).isEqualTo(0);
    }

    @Test
    @DisplayName(
            "Should return a list of Loans " +
            "when a user email and a status is passed as an argument to findByUserEmailAndActive method."
    )
    void shouldReturnAListOfLoans_whenAUserEmailAndAStatusIsPassedAsAnArgumentToFindByUserEmailAndActiveMethod() {
        var findLoans = loanRepository
                .findByUserEmailAndActive("luis@gmail.com", true, Pageable.unpaged());

        assertThat(findLoans).isNotNull();
        assertThat(findLoans.size()).isEqualTo(1);
        assertThat(findLoans.iterator().next().getUser().getEmail()).isEqualTo("luis@gmail.com");
        assertThat(findLoans.iterator().next().getBook().getTitle()).isEqualTo("O Senhor dos Anéis");

        findLoans = loanRepository
                .findByUserEmailAndActive("ana@gmail.com", true, Pageable.unpaged());

        assertThat(findLoans).isNotNull();
        assertThat(findLoans.size()).isEqualTo(0);
    }

    @Test
    @DisplayName(
            "Should return a book optional " +
            "when an user id and an user email is passed as an argument to findByIdAndUserEmail method."
    )
    void shouldReturnABookOptional_whenAnUserIdAndAnUserEmailIsPassedAsAnArgumentToFindByIdAndUserEmailMethod() {
        var findLoans = loanRepository.findByUserEmail("luis@gmail.com", Pageable.unpaged());
        var loanId = findLoans.iterator().next().getId();

        var loanOptional = loanRepository.findByIdAndUserEmail(loanId, "luis@gmail.com");

        assertThat(loanOptional.isPresent()).isTrue();
        assertThat(loanOptional.get().getBook().getTitle()).isEqualTo("O Senhor dos Anéis");
        assertThat(loanOptional.get().getUser().getEmail()).isEqualTo("luis@gmail.com");

        loanOptional = loanRepository.findByIdAndUserEmail(UUID.randomUUID(), "luis@gmail.com");

        assertThat(loanOptional.isPresent()).isFalse();

        loanOptional = loanRepository.findByIdAndUserEmail(loanId, "ana@gmail.com");

        assertThat(loanOptional.isPresent()).isFalse();
    }
}