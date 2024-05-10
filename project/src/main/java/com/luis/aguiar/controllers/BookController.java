package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.BookCreateDto;
import com.luis.aguiar.dto.BookResponseDto;
import com.luis.aguiar.mappers.BookMapper;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.services.BookService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("library/v1/books")
@Transactional
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
        Book result = service.findById(uuid);
        return ResponseEntity.status(HttpStatus.FOUND).body(BookMapper.toResponseDto(result));
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> findAllBooks() {
        List<Book> books = service.getAll();
        List<BookResponseDto> responseBooks = books.stream()
                .map(BookMapper::toResponseDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(responseBooks);
    }

    @GetMapping("/name")
    public ResponseEntity<List<BookResponseDto>> findAllByName(@RequestParam String name) {
        List<Book> books = service.getAllByName(name);
        List<BookResponseDto> booksDto = books.stream()
                .map(BookMapper::toResponseDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(booksDto);
    }

    @GetMapping("/author")
    public ResponseEntity<List<BookResponseDto>> findAllByAuthor(@RequestParam String firstName,
                                                                 @RequestParam String lastName) {
        List<Book> books = service.getAllByAuthor(firstName, lastName);
        List<BookResponseDto> bookDto = books.stream()
                .map(BookMapper::toResponseDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(bookDto);
    }

    @GetMapping("/status")
    public ResponseEntity<List<BookResponseDto>> findAllByStatus(@RequestParam Book.Status status) {
        List<Book> books = service.getAllByStatus(status);
        List<BookResponseDto> booksDto = books.stream()
                .map(BookMapper::toResponseDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(booksDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> updateBookData(@PathVariable(name = "id") UUID uuid,
                                                          @RequestBody @Valid BookCreateDto bookDto) {
        Book book = service.update(uuid, BookMapper.toBook(bookDto));
        return ResponseEntity.ok(BookMapper.toResponseDto(book));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteBook(@PathVariable(name = "id") UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.ok("Book deleted successfully.");
    }
}