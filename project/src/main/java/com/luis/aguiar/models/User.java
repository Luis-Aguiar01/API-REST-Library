package com.luis.aguiar.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Users")
@NoArgsConstructor @AllArgsConstructor
@Setter @Getter @ToString
@JsonPropertyOrder( { "id", "firstName", "lastName", "email", "password", "birthDate" } )
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "has_book_on_loan", nullable = false)
    private boolean hasBookOnLoan;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}