package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.*;
import com.luis.aguiar.services.LoanService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("library/v1/loans")
public class LoanController {
    @Autowired
    private LoanService loanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<LoanResponseDto> createNewLoan(@RequestBody @Valid LoanRequestDto loanRequest) {
        LoanResponseDto loan = loanService.saveLoan(loanRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loan);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponseDto> findLoanById(@PathVariable(name = "id") UUID id) {
        LoanResponseDto loan = loanService.findById(id);
        return ResponseEntity.status(HttpStatus.FOUND).body(loan);
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanResponseDto>> findByStatus(@PathParam(value = "status") Boolean status) {
        List<LoanResponseDto> loan = loanService.findByStatus(status);
        return ResponseEntity.status(HttpStatus.OK).body(loan);
    }

    @GetMapping("/user/{email}")
    @PreAuthorize("hasAnyRole('ADMIN','USER') AND #email == authentication.principal.username")
    public ResponseEntity<List<LoanResponseDto>> findLoanByUser(@PathVariable(name = "email") String email) {
        List<LoanResponseDto> loans = loanService.findByUserEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(loans);
    }

    @GetMapping("/user/{email}/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') AND #email == authentication.principal.username")
    public ResponseEntity<List<LoanResponseDto>> findByUserAndStatus(@PathVariable(name = "email") String email,
                                                                     @PathVariable(name = "status") Boolean status) {
        List<LoanResponseDto> loans = loanService.findByUserAndActive(email, status);
        return ResponseEntity.status(HttpStatus.OK).body(loans);
    }

    @PostMapping("/return/{email}/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') AND #email == authentication.principal.username")
    public ResponseEntity<Void> returnLoan(@PathVariable(name = "id") UUID id) {
        loanService.returnLoan(id);
        return ResponseEntity.ok().build();
    }
}