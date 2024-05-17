package com.luis.aguiar.dto;

import com.fasterxml.jackson.annotation.*;
import com.luis.aguiar.enums.Status;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
@JsonPropertyOrder( { "id", "title", "authors", "publicationDate", "status" } )
public class BookResponseDto extends RepresentationModel<BookResponseDto> {

    @NotNull
    private UUID id;

    @NotBlank
    private String title;

    @NotNull
    private LocalDate publicationDate;

    @NotNull
    private Status status;

    @JsonManagedReference
    private Set<Link> authors = new HashSet<>();
}