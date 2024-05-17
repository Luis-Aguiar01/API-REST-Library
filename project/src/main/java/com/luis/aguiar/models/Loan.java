package com.luis.aguiar.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "loans")
@NoArgsConstructor @AllArgsConstructor
@Setter @Getter @ToString
@Builder
@EqualsAndHashCode(of = "id")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne
    private Book book;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    private LocalDate loanDate;

    @NotNull
    private LocalDate returnDate;

    @NotNull
    private Boolean active;
}