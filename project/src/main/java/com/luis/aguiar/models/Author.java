package com.luis.aguiar.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "authors")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
@EqualsAndHashCode(of = "id")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @NotNull
    @Column(nullable = false)
    private LocalDate birthDate;

    @NotBlank
    @Column(nullable = false)
    private String nationality;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();
}