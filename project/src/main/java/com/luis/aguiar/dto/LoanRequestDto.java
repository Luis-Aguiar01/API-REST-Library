package com.luis.aguiar.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequestDto {
    @NotNull
    private UUID book_id;

    @NotNull
    private String email;
}