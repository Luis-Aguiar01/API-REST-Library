package com.luis.aguiar.repositories;

import com.luis.aguiar.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByActive(Boolean status);

    List<Loan> findByUserEmail(String email);

    List<Loan> findByUserEmailAndActive(String email, Boolean active);
}