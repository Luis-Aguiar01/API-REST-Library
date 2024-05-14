package com.luis.aguiar.dto;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
@JsonPropertyOrder( { "firstName", "lastName", "birthDate", "nationality", "books" } )
public class AuthorResponseDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private LocalDate birthDate;

    @NotBlank
    private String nationality;

    @JsonBackReference
    private Set<BookResponseDto> books = Collections.emptySet();
}