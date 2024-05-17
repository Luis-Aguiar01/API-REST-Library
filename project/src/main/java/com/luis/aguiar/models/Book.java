package com.luis.aguiar.models;

import com.luis.aguiar.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "books")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
@EqualsAndHashCode(of = "id")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String title;

    @ManyToMany
    @JoinTable(
            name = "books_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book")
    private Set<Loan> loans = new HashSet<>();

    @NotNull
    @Column(nullable = false)
    private LocalDate publicationDate;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
}