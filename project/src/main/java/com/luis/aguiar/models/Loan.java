package com.luis.aguiar.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Loans")
@NoArgsConstructor @AllArgsConstructor
@Setter @Getter @ToString
@JsonPropertyOrder( { "id", "book", "user", "loanDate", "returnDate" } )
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @OneToOne
    private Book book;

    @NotNull
    @OneToOne
    private User user;

    @NotNull
    private LocalDate loanDate;

    @NotNull
    private LocalDate returnDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan loan)) return false;
        return Objects.equals(getId(), loan.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}