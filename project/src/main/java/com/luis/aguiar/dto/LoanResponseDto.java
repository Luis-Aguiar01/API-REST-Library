package com.luis.aguiar.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanResponseDto extends RepresentationModel<LoanResponseDto> {
    @NotNull
    private UUID id;

    @NotNull
    private BookResponseDto book;

    @NotNull
    @JsonIgnore
    private UserResponseDto user;

    @NotNull
    private LocalDate loanDate;

    @NotNull
    private LocalDate returnDate;

    @NotNull
    private Boolean active;
}
