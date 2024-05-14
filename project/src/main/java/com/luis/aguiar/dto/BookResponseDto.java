package com.luis.aguiar.dto;

import com.fasterxml.jackson.annotation.*;
import com.luis.aguiar.enums.Status;
import jakarta.validation.constraints.*;
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
    private Status status;

    @JsonManagedReference
    private Set<AuthorResponseDto> authors = Collections.emptySet();
}
