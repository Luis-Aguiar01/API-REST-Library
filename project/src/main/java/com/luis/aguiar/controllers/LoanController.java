package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.LoanRequestDto;
import com.luis.aguiar.dto.LoanResponseDto;
import com.luis.aguiar.mappers.LoanMapper;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.models.Loan;
import com.luis.aguiar.models.User;
import com.luis.aguiar.repositories.UserRepository;
import com.luis.aguiar.services.BookService;
import com.luis.aguiar.services.LoanService;
import com.luis.aguiar.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("library/v1/loans")
public class LoanController {
    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponseDto> createNewLoan(@RequestBody @Valid LoanRequestDto loanRequest) {
        LoanResponseDto loan = loanService.saveLoan(loanRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loan);
    }
}