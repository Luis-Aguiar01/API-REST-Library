package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.*;
import com.luis.aguiar.exceptions.ErrorModel;
import com.luis.aguiar.exceptions.UnauthorizedException;
import com.luis.aguiar.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Loans", description = "Fornece as operações relacionadas aos empréstimos de livros.")
@RestController
@RequestMapping("library/v1/loans")
public class LoanController {
    @Autowired
    private LoanService loanService;

    @Operation(summary = "Cria um novo empréstimo.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Empréstimo criado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = LoanResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem permissão ou dados inválidos fornecidos.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Livro indisponível.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                )
    })
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

    @Operation(summary = "Encontra um empréstimo pelo ID.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "302",
                        description = "Empréstimo encontrado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = LoanResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem permissão ou dados inválidos fornecidos.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "ID com formato inválido passado para a requisição.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Empréstimo não encontrado.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponseDto> findLoanById(@PathVariable(name = "id") UUID id) {
        LoanResponseDto loan = loanService.findById(id);
        return ResponseEntity.status(HttpStatus.FOUND).body(loan);
    }

    @Operation(summary = "Encontra empréstimos pelo status.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Empréstimo encontrado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = LoanResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem permissão ou dados inválidos fornecidos.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Dados inválidos enviados no corpo da requisição.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                )
    })
    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanResponseDto>> findByActive(@RequestParam boolean status,
                                                              @RequestParam int page,
                                                              @RequestParam int quantity) {
        List<LoanResponseDto> loan = loanService.findByStatus(status, page, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(loan);
    }


    @Operation(summary = "Encontra empréstimos associados ao e-mail do usuário.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Empréstimo encontrado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = LoanResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem permissão ou dados inválidos fornecidos.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Dados inválidos enviados no corpo da requisição.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                )
    })
    @GetMapping("/user/{email}")
    @PreAuthorize("hasAnyRole('ADMIN','USER') AND #email == authentication.principal.username")
    public ResponseEntity<List<LoanResponseDto>> findLoanByUser(@PathVariable(name = "email") String email,
                                                                @RequestParam int page,
                                                                @RequestParam int quantity) {
        List<LoanResponseDto> loans = loanService.findByUserEmail(email, page, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(loans);
    }


    @Operation(summary = "Encontra empréstimos associados ao e-mail do usuário e status do empréstimo.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Empréstimo encontrado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = LoanResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem permissão ou dados inválidos fornecidos.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Dados inválidos enviados no corpo da requisição.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                )
    })
    @GetMapping("/user/{email}/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') AND #email == authentication.principal.username")
    public ResponseEntity<List<LoanResponseDto>> findByUserAndStatus(@PathVariable(name = "email") String email,
                                                                     @PathVariable(name = "status") boolean status,
                                                                     @RequestParam int page,
                                                                     @RequestParam int quantity) {
        List<LoanResponseDto> loans = loanService.findByUserAndActive(email, status, page, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(loans);
    }


    @Operation(summary = "Devolve um empréstimo ativo.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Empréstimo devolvido com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = LoanResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem permissão ou dados inválidos fornecidos.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Dados inválidos enviados no corpo da requisição.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Empréstimo não encontrado.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Empréstimo inativo.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                )
    })
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