package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.*;
import com.luis.aguiar.exceptions.ErrorModel;
import com.luis.aguiar.mappers.AuthorMapper;
import com.luis.aguiar.models.Author;
import com.luis.aguiar.services.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Authors", description = "Fornece as operações realizadas em autores na API.")
@RestController
@RequestMapping("library/v1/authors")
public class AuthorController {

    @Autowired
    private AuthorService service;

    @Operation(summary = "Cria um novo autor.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                    responseCode = "201",
                    description = "Autor criado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthorResponseDto.class))
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
                )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> createAuthor(@RequestBody @Valid AuthorCreateDto authorDto) {
        Author author = service.save(AuthorMapper.toAuthor(authorDto));
        AuthorResponseDto authorResponseDto = AuthorMapper.toResponseDto(author);

        addDeleteAuthorReference(authorResponseDto, author.getId());
        addUpdateAuthorReference(authorResponseDto, author.getId());
        addAssociateAuthorWithBookReference(authorResponseDto, author.getId());
        addFindByIdReference(authorResponseDto, author.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(authorResponseDto);
    }

    @Operation(summary = "Recebe uma lista de todos os autores cadastrados.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurso retornado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato inválido para os dados da requisição.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorModel.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<AuthorResponseDto>> findAllAuthors(@RequestParam int page,
                                                                  @RequestParam int quantity) {
        List<AuthorResponseDto> authors = service.findAll(page, quantity);

        authors.forEach(author -> {
            addFindByIdReference(author, author.getId());
            addDeleteAuthorReference(author, author.getId());
            addUpdateAuthorReference(author, author.getId());
            addAssociateAuthorWithBookReference(author, author.getId());
        });

        return ResponseEntity.status(HttpStatus.OK).body(authors);
    }

    @Operation(summary = "Encontra um autor pelo ID e exibe os seus dados.", responses = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Recurso encontrado e retornado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Autor não encontrado.",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorModel.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato inválido para o ID",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorModel.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> findAuthorById(@PathVariable(name = "id") UUID uuid) {
        AuthorResponseDto author = service.findById(uuid);

        addDeleteAuthorReference(author, author.getId());
        addUpdateAuthorReference(author, author.getId());
        addAssociateAuthorWithBookReference(author, author.getId());

        return ResponseEntity.status(HttpStatus.FOUND).body(author);
    }

    @Operation(summary = "Atualiza as informações de um autor.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Dados atualizados com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = AuthorResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Autor não encontrado.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Formato inválido para o ID",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem acesso a operação ou dados fornecidos inválidos.",
                        content = @Content
                ),
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> updateAuthorData(@PathVariable(name = "id") UUID uuid,
                                                              @RequestBody @Valid AuthorCreateDto authorDto) {
        AuthorResponseDto author = service.update(uuid, AuthorMapper.toAuthor(authorDto));

        addDeleteAuthorReference(author, uuid);
        addAssociateAuthorWithBookReference(author, uuid);
        addFindByIdReference(author, uuid);

        return ResponseEntity.ok(author);
    }

    @Operation(summary = "Deleta um autor.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Autor deletado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = String.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Autor não encontrado.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Formato inválido para o ID",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem acesso a operação ou dados fornecidos inválidos.",
                        content = @Content
                ),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteAuthor(@PathVariable(name = "id") UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.ok("Author deleted successfully.");
    }

    @Operation(summary = "Associa um autor a um livro (autoria).",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Operação realizada com sucesso.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Autor não encontrado ou livro não encontrado.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Formato inválido para o ID",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem acesso a operação ou dados fornecidos inválidos.",
                        content = @Content
                ),
    })
    @PostMapping("/{authorID}/{bookID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> associateAuthorWithBook(@PathVariable(name = "bookID") UUID bookID,
                                                        @PathVariable(name = "authorID") UUID authorID) {
        service.associateAuthorWithBook(bookID, authorID);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private void addFindByIdReference(AuthorResponseDto authorResponseDto, UUID uuid) {
        authorResponseDto.add(linkTo(methodOn(AuthorController.class)
                .findAuthorById(uuid))
                .withSelfRel()
                .withType(HttpMethod.GET.name()));
    }

    private void addUpdateAuthorReference(AuthorResponseDto authorResponseDto, UUID uuid) {
        authorResponseDto.add(linkTo(methodOn(AuthorController.class)
                .updateAuthorData(uuid, null))
                .withRel("update")
                .withType(HttpMethod.PUT.name()));
    }

    private void addDeleteAuthorReference(AuthorResponseDto authorResponseDto, UUID uuid) {
        authorResponseDto.add(linkTo(methodOn(AuthorController.class)
                .deleteAuthor(uuid))
                .withRel("delete")
                .withType(HttpMethod.DELETE.name()));
    }

    private void addAssociateAuthorWithBookReference(AuthorResponseDto authorResponseDto, UUID uuid) {
        authorResponseDto.add(linkTo(methodOn(AuthorController.class)
                .associateAuthorWithBook(UUID.randomUUID(), uuid))
                .withRel("associate-with-book")
                .withType(HttpMethod.POST.name()));
    }
}