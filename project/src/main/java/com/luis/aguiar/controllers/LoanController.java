package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.*;
import com.luis.aguiar.exceptions.UnauthorizedException;
import com.luis.aguiar.services.LoanService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("library/v1/loans")
public class LoanController {
    @Autowired
    private LoanService loanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<LoanResponseDto> createNewLoan(@RequestBody @Valid LoanRequestDto loanRequest,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        if (!loanRequest.getEmail().equals(userDetails.getUsername())) {
            throw new UnauthorizedException("You do not have permission to access this profile.");
        }

        LoanResponseDto loan = loanService.saveLoan(loanRequest);
        addAllReferences(loan);

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
    public ResponseEntity<List<LoanResponseDto>> findByActive(@RequestParam boolean status,
                                                              @RequestParam int page,
                                                              @RequestParam int quantity) {
        List<LoanResponseDto> loan = loanService.findByStatus(status, page, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(loan);
    }

    @GetMapping("/user/{email}")
    @PreAuthorize("hasAnyRole('ADMIN','USER') AND #email == authentication.principal.username")
    public ResponseEntity<List<LoanResponseDto>> findLoanByUser(@PathVariable(name = "email") String email,
                                                                @RequestParam int page,
                                                                @RequestParam int quantity) {
        List<LoanResponseDto> loans = loanService.findByUserEmail(email, page, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(loans);
    }

    @GetMapping("/user/{email}/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') AND #email == authentication.principal.username")
    public ResponseEntity<List<LoanResponseDto>> findByUserAndStatus(@PathVariable(name = "email") String email,
                                                                     @PathVariable(name = "status") boolean status,
                                                                     @RequestParam int page,
                                                                     @RequestParam int quantity) {
        List<LoanResponseDto> loans = loanService.findByUserAndActive(email, status, page, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(loans);
    }

    @PostMapping("/return/{email}/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') AND #email == authentication.principal.username")
    public ResponseEntity<Void> returnLoan(@PathVariable(name = "id") UUID id,
                                           @PathVariable(name = "email") String email) {
        loanService.returnLoan(id);
        return ResponseEntity.ok().build();
    }

    private static void addReturnLoanReference(LoanResponseDto loan) {
        loan.add(linkTo(methodOn(LoanController.class)
                .returnLoan(loan.getId(), null))
                .withRel("return-loan")
                .withType(HttpMethod.POST.name()));
    }

    private static void addFindByUserAndStatusReference(LoanResponseDto loan) {
        loan.add(linkTo(methodOn(LoanController.class)
                .findByUserAndStatus(loan.getUser().getEmail(), true, 0, 1))
                .withRel("find-by-user-and-active")
                .withType(HttpMethod.GET.name()));
    }

    private static void addFindLoanByUserReference(LoanResponseDto loan) {
        loan.add(linkTo(methodOn(LoanController.class)
                .findLoanByUser(loan.getUser().getEmail(),1 ,1))
                .withRel("find-by-user")
                .withType(HttpMethod.GET.name()));
    }

    private static void addFinByActiveReference(LoanResponseDto loan) {
        loan.add(linkTo(methodOn(LoanController.class)
                .findByActive(true, 0, 1))
                .withRel("find-by-active")
                .withType(HttpMethod.GET.name()));
    }

    private static void addFindLoanByIdReference(LoanResponseDto loan) {
        loan.add(linkTo(methodOn(LoanController.class)
                .findLoanById(loan.getId()))
                .withSelfRel()
                .withType(HttpMethod.GET.name()));
    }

    private static void addAllReferences(LoanResponseDto loan) {
        addFindLoanByIdReference(loan);
        addFinByActiveReference(loan);
        addFindLoanByUserReference(loan);
        addFindByUserAndStatusReference(loan);
        addReturnLoanReference(loan);
    }
}