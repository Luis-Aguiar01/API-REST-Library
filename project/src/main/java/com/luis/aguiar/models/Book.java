package com.luis.aguiar.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Books")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
@EqualsAndHashCode(of = "id")
@JsonPropertyOrder({ "id, title, author, publicationDate, status" })
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @ManyToMany
    @JoinTable(
            name = "books_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = Collections.emptySet();

    @NotNull
    @Column(name = "publication_date", nullable = false)
    private LocalDate publicationDate;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status { AVAILABLE, UNAVAILABLE };
}