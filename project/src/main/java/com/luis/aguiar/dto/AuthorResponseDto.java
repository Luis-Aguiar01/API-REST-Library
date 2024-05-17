package com.luis.aguiar.dto;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
@JsonPropertyOrder( { "id", "firstName", "lastName", "birthDate", "nationality", "books" } )
public class AuthorResponseDto extends RepresentationModel<AuthorResponseDto> {

    @NotNull
    private UUID id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private LocalDate birthDate;

    @NotBlank
    private String nationality;

    @JsonBackReference
    private Set<BookResponseDto> books = new HashSet<>();
}