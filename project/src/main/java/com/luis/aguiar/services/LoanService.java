package com.luis.aguiar.services;

import com.luis.aguiar.dto.LoanRequestDto;
import com.luis.aguiar.dto.LoanResponseDto;
import com.luis.aguiar.exceptions.BookNotAvailableException;
import com.luis.aguiar.exceptions.EntityNotFoundException;
import com.luis.aguiar.exceptions.LoanNotAvailableException;
import com.luis.aguiar.mappers.LoanMapper;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.models.Loan;
import com.luis.aguiar.models.User;
import com.luis.aguiar.repositories.BookRepository;
import com.luis.aguiar.repositories.LoanRepository;
import com.luis.aguiar.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

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
        Book book = bookRepository.findById(loanRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("No books with this ID could be found."));

        if (book.getStatus() == Book.Status.UNAVAILABLE) {
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
                .isActive(true)
                .build();
        loanRepository.save(loan);

        book.setStatus(Book.Status.UNAVAILABLE);
        user.setHasBookOnLoan(false);

        return LoanMapper.toResponseDto(loan);
    }
}
