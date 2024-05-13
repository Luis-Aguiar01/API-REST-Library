package com.luis.aguiar.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.luis.aguiar.models.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
@JsonPropertyOrder( { "id", "title", "authors", "publicationDate", "status" } )
public class BookResponseDto {

    @NotNull
    private UUID id;

    @NotBlank
    private String title;

    @NotNull
    private LocalDate publicationDate;

    @NotNull
    private Book.Status status;

    @JsonManagedReference
    private Set<AuthorResponseDto> authors = Collections.emptySet();
}
