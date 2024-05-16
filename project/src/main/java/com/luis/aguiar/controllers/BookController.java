package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.*;
import com.luis.aguiar.enums.Status;
import com.luis.aguiar.mappers.BookMapper;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.services.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("library/v1/books")
public class BookController {

    @Autowired
    private BookService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> createBook(@RequestBody @Valid BookCreateDto bookCreateDto) {
        Book book = service.save(BookMapper.toBook(bookCreateDto));
        BookResponseDto bookResponseDto = BookMapper.toResponseDto(book);

        addAllReferences(bookResponseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> findBookById(@PathVariable(name = "id") UUID uuid) {
        BookResponseDto book = service.findById(uuid);

        addDeleteBookReference(book);
        addUpdateBookDataReference(book, book.getId());

        return ResponseEntity.status(HttpStatus.FOUND).body(book);
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> findAllBooks(@RequestParam int page, @RequestParam int quantity) {
        List<BookResponseDto> books = service.getAll(page, quantity);

        books.forEach(BookController::addFindByIdReference);

        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @GetMapping("/name")
    public ResponseEntity<List<BookResponseDto>> findAllByBookName(@RequestParam String name,
                                                                   @RequestParam int page,
                                                                   @RequestParam int quantity) {
        List<BookResponseDto> books = service.getAllByName(name, page, quantity);
        books.forEach(BookController::addFindByIdReference);

        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @GetMapping("/author")
    public ResponseEntity<List<BookResponseDto>> findAllByAuthorLastName(@RequestParam String lastName,
                                                                         @RequestParam int page,
                                                                         @RequestParam int quantity) {
        List<BookResponseDto> books = service.getAllByAuthor(lastName, page, quantity);
        books.forEach(BookController::addFindByIdReference);

        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @GetMapping("/status")
    public ResponseEntity<List<BookResponseDto>> findAllByStatus(@RequestParam Status status,
                                                                 @RequestParam int page,
                                                                 @RequestParam int quantity) {
        List<BookResponseDto> books = service.getAllByStatus(status, page, quantity);
        books.forEach(BookController::addFindByIdReference);

        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> updateBookData(@PathVariable(name = "id") UUID uuid,
                                                          @RequestBody @Valid BookCreateDto bookDto) {
        BookResponseDto book = service.update(uuid, BookMapper.toBook(bookDto));

        addFindByIdReference(book);
        addDeleteBookReference(book);

        return ResponseEntity.ok(book);
    }

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