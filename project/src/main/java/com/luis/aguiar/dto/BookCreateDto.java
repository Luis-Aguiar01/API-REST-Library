package com.luis.aguiar.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.luis.aguiar.models.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
@JsonPropertyOrder( { "title", "publicationDate", "status" } )
public class BookCreateDto {

    @NotBlank
    private String title;

    @NotNull
    private LocalDate publicationDate;

    @NotNull
    private Book.Status status;
}