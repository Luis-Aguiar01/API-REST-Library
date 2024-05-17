package com.luis.aguiar.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.luis.aguiar.enums.Status;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class BookCreateDto {

    @NotBlank
    private String title;

    @NotNull
    private LocalDate publicationDate;

    @NotNull
    private Status status;
}