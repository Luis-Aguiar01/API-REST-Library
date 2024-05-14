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

@RestController
@RequestMapping("library/v1/books")
public class BookController {

    @Autowired
    private BookService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> createBook(@RequestBody @Valid BookCreateDto bookCreateDto) {
        Book result = service.save(BookMapper.toBook(bookCreateDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(BookMapper.toResponseDto(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> findBookById(@PathVariable(name = "id") UUID uuid) {
        BookResponseDto result = service.findById(uuid);
        return ResponseEntity.status(HttpStatus.FOUND).body(result);
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> findAllBooks() {
        List<BookResponseDto> books = service.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @GetMapping("/name")
    public ResponseEntity<List<BookResponseDto>> findAllByName(@RequestParam String name) {
        List<BookResponseDto> books = service.getAllByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @GetMapping("/author")
    public ResponseEntity<List<BookResponseDto>> findAllByAuthor(@RequestParam String firstName,
                                                                 @RequestParam String lastName) {
        List<BookResponseDto> books = service.getAllByAuthor(firstName, lastName);
        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @GetMapping("/status")
    public ResponseEntity<List<BookResponseDto>> findAllByStatus(@RequestParam Status status) {
        List<BookResponseDto> books = service.getAllByStatus(status);
        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> updateBookData(@PathVariable(name = "id") UUID uuid,
                                                          @RequestBody @Valid BookCreateDto bookDto) {
        BookResponseDto book = service.update(uuid, BookMapper.toBook(bookDto));
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteBook(@PathVariable(name = "id") UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.ok("Book deleted successfully.");
    }
}