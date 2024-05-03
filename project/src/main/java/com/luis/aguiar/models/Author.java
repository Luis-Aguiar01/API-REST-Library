package com.luis.aguiar.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Authors")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
@JsonPropertyOrder( { "id", "firstName", "lastName", "birthDate", "nationality" } )
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @NotBlank
    @Column(name = "nationality")
    private String nationality;

    @NotNull @NotEmpty
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author author)) return false;
        return Objects.equals(getId(), author.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}