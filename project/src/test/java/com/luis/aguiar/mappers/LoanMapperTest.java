package com.luis.aguiar.mappers;

import com.luis.aguiar.dto.LoanResponseDto;
import com.luis.aguiar.enums.Role;
import com.luis.aguiar.enums.Status;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.models.Loan;
import com.luis.aguiar.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class LoanMapperTest {

    private Loan loan;
    private User user;
    private Book book;

    @BeforeEach
    void setup() {
        book = new Book(
            UUID.randomUUID(),
            "O Senhor dos Anéis",
            new HashSet<>(),
            new HashSet<>(),
            LocalDate.of(1920, 10, 10),
            Status.AVAILABLE
        );
        user = new User(
            UUID.randomUUID(),
            "Luis",
            "Aguiar",
            "luis@gmail.com",
            "123456",
            LocalDate.of(2000, 10, 10),
            true,
            Role.USER,
            new HashSet<>()
        );
        loan = Loan.builder()
            .id(UUID.randomUUID())
            .loanDate(LocalDate.now())
            .returnDate(LocalDate.now().plusDays(7))
            .active(true)
            .user(user)
            .book(book)
            .build();
    }

    @Test
    @DisplayName("Should return a LoanResponseDto when a valid Loan is passed as an argument to toLoanResponse method.")
    void shouldReturnALoanResponseDto_whenAValidLoanIsPassedAsAnArgumentToToLoanResponseMethod() {
        LoanResponseDto responseDto = LoanMapper.toResponseDto(loan);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(loan.getId());
        assertThat(responseDto.getLoanDate()).isEqualTo(loan.getLoanDate());
        assertThat(responseDto.getUser().getEmail()).isEqualTo(loan.getUser().getEmail());
        assertThat(responseDto.getReturnDate()).isEqualTo(loan.getReturnDate());
        assertThat(responseDto.getActive()).isEqualTo(loan.getActive());
        assertThat(responseDto.getBook().getTitle()).isEqualTo(loan.getBook().getTitle());
    }

    @Test
    @DisplayName("Should throw an Exception when a null value is passed as an argument to toLoanResponse method.")
    void shouldThrowAnException_whenANullValueIsPassedAsAnArgumentToToLoanResponseMethod() {
        assertThatThrownBy(() -> LoanMapper.toResponseDto(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The Loan can't be null.");
    }
}