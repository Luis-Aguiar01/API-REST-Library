package com.luis.aguiar.dto;

import com.luis.aguiar.models.Book;
import com.luis.aguiar.models.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanResponseDto {
    @NotNull
    private BookResponseDto book;

    @NotNull
    private UserResponseDto user;

    @NotNull
    private LocalDate loanDate;

    @NotNull
    private LocalDate returnDate;

    @NotNull
    private Boolean isActive;
}
