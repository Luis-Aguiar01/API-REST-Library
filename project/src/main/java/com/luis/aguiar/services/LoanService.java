package com.luis.aguiar.services;

import com.luis.aguiar.dto.*;
import com.luis.aguiar.enums.Status;
import com.luis.aguiar.exceptions.*;
import com.luis.aguiar.mappers.LoanMapper;
import com.luis.aguiar.models.*;
import com.luis.aguiar.repositories.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public LoanResponseDto saveLoan(@Valid LoanRequestDto loanRequest) {
        Book book = bookRepository.findById(loanRequest.getBook_id())
                .orElseThrow(() -> new EntityNotFoundException("No books with this ID could be found."));

        if (book.getStatus() == Status.UNAVAILABLE) {
            throw new BookNotAvailableException("The book is not available.");
        }

        User user = userRepository.findByEmail(loanRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("No user with this ID could be found."));

        if (!user.getHasBookOnLoan()) {
            throw new LoanNotAvailableException("You already have an active loan.");
        }

        LocalDate today = LocalDate.now();
        LocalDate todayPlusSevenDays = LocalDate.now().plusDays(7);

        Loan loan = Loan.builder()
                .book(book)
                .user(user)
                .loanDate(today)
                .returnDate(todayPlusSevenDays)
                .active(true)
                .build();
        Loan savedLoan = loanRepository.save(loan);

        book.setStatus(Status.UNAVAILABLE);
        user.setHasBookOnLoan(false);

        return LoanMapper.toResponseDto(savedLoan);
    }

    @Transactional
    public LoanResponseDto findById(UUID id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No loan with this ID could be found."));

        return LoanMapper.toResponseDto(loan);
    }

    @Transactional
    public List<LoanResponseDto> findByStatus(boolean status, int page, int quantity) {
        List<Loan> loans = loanRepository.findByActive(status, PageRequest.of(page, quantity));

        return loans.stream()
                .map(LoanMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public List<LoanResponseDto> findByUserEmail(String email, int page, int quantity) {
        List<Loan> loans = loanRepository.findByUserEmail(email, PageRequest.of(page, quantity));

        return loans.stream()
                .map(LoanMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public List<LoanResponseDto> findByUserAndActive(String email, boolean status, int page, int quantity) {
        List<Loan> loans = loanRepository.findByUserEmailAndActive(email, status, PageRequest.of(page, quantity));

        return loans.stream()
                .map(LoanMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public void returnLoan(UUID id, String email) {
        Loan loan = loanRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(
                        () -> new EntityNotFoundException("No loans with this ID can be found associated with this email.")
                );

        if (!loan.getActive()) {
            throw new LoanNotAvailableException("The loan has already been returned.");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setActive(false);

        Book book = loan.getBook();
        book.setStatus(Status.AVAILABLE);
        bookRepository.save(book);

        User user = loan.getUser();
        user.setHasBookOnLoan(true);
        userRepository.save(user);
    }
}