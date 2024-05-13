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
@Builder
@EqualsAndHashCode(of = "id")
@JsonPropertyOrder( { "id", "book", "user", "loanDate", "returnDate", "isActive" } )
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

    @NotNull
    private Boolean active;
}