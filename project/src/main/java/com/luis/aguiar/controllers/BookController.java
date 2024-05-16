package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.*;
import com.luis.aguiar.enums.Status;
import com.luis.aguiar.exceptions.ErrorModel;
import com.luis.aguiar.mappers.BookMapper;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.services.BookService;
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

@Tag(name = "Books", description = "Operações realizadas nos livros da API.")
@RestController
@RequestMapping("library/v1/books")
public class BookController {

    @Autowired
    private BookService service;

    @Operation(summary = "Cria um novo livro.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Livro criado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = BookResponseDto.class))
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
    public ResponseEntity<BookResponseDto> createBook(@RequestBody @Valid BookCreateDto bookCreateDto) {
        Book book = service.save(BookMapper.toBook(bookCreateDto));
        BookResponseDto bookResponseDto = BookMapper.toResponseDto(book);

        addAllReferences(bookResponseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponseDto);
    }

    @Operation(summary = "Encontra um livro cadastrado na API pelo ID.", responses = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Livro encontrado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookResponseDto.class))
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
                    description = "Formato inválido para os dados da requisição",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorModel.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> findBookById(@PathVariable(name = "id") UUID uuid) {
        BookResponseDto book = service.findById(uuid);

        addDeleteBookReference(book);
        addUpdateBookDataReference(book, book.getId());

        return ResponseEntity.status(HttpStatus.FOUND).body(book);
    }


    @Operation(summary = "Recebe uma lista de todos os livros cadastrados.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurso retornado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookResponseDto.class))
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
    public ResponseEntity<List<BookResponseDto>> findAllBooks(@RequestParam int page,
                                                              @RequestParam int quantity) {
        List<BookResponseDto> books = service.getAll(page, quantity);
        books.forEach(BookController::addFindByIdReference);

        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @Operation(summary = "Encontra e retorna livros que condizem com o nome passado para a requisição.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Recurso retornado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = BookResponseDto.class))
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
    @GetMapping("/name")
    public ResponseEntity<List<BookResponseDto>> findAllByBookName(@RequestParam String name,
                                                                   @RequestParam int page,
                                                                   @RequestParam int quantity) {
        List<BookResponseDto> books = service.getAllByName(name, page, quantity);
        books.forEach(BookController::addFindByIdReference);

        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @Operation(summary = "Encontra e retorna livros que condizem com o sobrenome do autor passado para a requisição.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Recurso retornado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = BookResponseDto.class))
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
    @GetMapping("/author")
    public ResponseEntity<List<BookResponseDto>> findAllByAuthorLastName(@RequestParam String lastName,
                                                                         @RequestParam int page,
                                                                         @RequestParam int quantity) {
        List<BookResponseDto> books = service.getAllByAuthor(lastName, page, quantity);
        books.forEach(BookController::addFindByIdReference);

        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @Operation(summary = "Encontra e retorna livros que condizem com o status do livro passado para a requisição.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Recurso retornado com sucesso.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookResponseDto.class))
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
    @GetMapping("/status")
    public ResponseEntity<List<BookResponseDto>> findAllByStatus(@RequestParam Status status,
                                                                 @RequestParam int page,
                                                                 @RequestParam int quantity) {
        List<BookResponseDto> books = service.getAllByStatus(status, page, quantity);
        books.forEach(BookController::addFindByIdReference);

        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @Operation(summary = "Atualiza as informações de um livro.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Dados atualizados com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = BookResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Livro não encontrado.",
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
                        description = "Usuário não autenticado, sem acesso a operação ou dados inválidos fornecidos.",
                        content = @Content
                ),
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> updateBookData(@PathVariable(name = "id") UUID uuid,
                                                          @RequestBody @Valid BookCreateDto bookDto) {
        BookResponseDto book = service.update(uuid, BookMapper.toBook(bookDto));

        addFindByIdReference(book);
        addDeleteBookReference(book);

        return ResponseEntity.ok(book);
    }

    @Operation(summary = "Deleta um livro.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Livro deletado com sucesso.",
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
                        description = "Livro não encontrado.",
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
    public ResponseEntity<Object> deleteBook(@PathVariable(name = "id") UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.ok("Book deleted successfully.");
    }

    private static void addFindAllByBookNameReference(BookResponseDto bookResponseDto) {
        bookResponseDto.add(linkTo(methodOn(BookController.class)
                .findAllByBookName("Harry Potter", 1, 2))
                .withRel("find-all-by-book-name")
                .withType(HttpMethod.GET.name()));
    }

    private static void addFindAllByStatusReference(BookResponseDto bookResponseDto) {
        bookResponseDto.add(linkTo(methodOn(BookController.class)
                .findAllByStatus(Status.AVAILABLE, 0, 1))
                .withRel("find-all-by-status")
                .withType(HttpMethod.GET.name()));
    }

    private static void addFindAllByAuthorLastNameReference(BookResponseDto bookResponseDto) {
        bookResponseDto.add(linkTo(methodOn(BookController.class)
                .findAllByAuthorLastName("Tolkien", 0, 1))
                .withRel("find-by-author-lastName")
                .withType(HttpMethod.GET.name()));
    }

    private static void addFindAllBooksReference(BookResponseDto bookResponseDto) {
        bookResponseDto.add(linkTo(methodOn(BookController.class)
                .findAllBooks(1, 2))
                .withRel("find-all")
                .withType(HttpMethod.GET.name()));
    }

    private static void addUpdateBookDataReference(BookResponseDto bookResponseDto, UUID uuid) {
        bookResponseDto.add(linkTo(methodOn(BookController.class)
                .updateBookData(uuid, new BookCreateDto()))
                .withRel("update")
                .withType(HttpMethod.PUT.name()));
    }

    private static void addDeleteBookReference(BookResponseDto bookResponseDto) {
        bookResponseDto.add(linkTo(methodOn(BookController.class)
                .deleteBook(bookResponseDto.getId()))
                .withRel("delete")
                .withType(HttpMethod.DELETE.name()));
    }

    private static void addFindByIdReference(BookResponseDto bookResponseDto) {
        bookResponseDto.add(linkTo(methodOn(BookController.class)
                .findBookById(bookResponseDto.getId()))
                .withSelfRel()
                .withType(HttpMethod.GET.name()));
    }

    private static void addAllReferences(BookResponseDto bookResponseDto) {
        addFindByIdReference(bookResponseDto);
        addDeleteBookReference(bookResponseDto);
        addUpdateBookDataReference(bookResponseDto, bookResponseDto.getId());
        addFindAllBooksReference(bookResponseDto);
        addFindAllByAuthorLastNameReference(bookResponseDto);
        addFindAllByStatusReference(bookResponseDto);
        addFindAllByBookNameReference(bookResponseDto);
    }
}