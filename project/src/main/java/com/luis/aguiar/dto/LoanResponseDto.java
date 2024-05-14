package com.luis.aguiar.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
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
    private Boolean active;
}
