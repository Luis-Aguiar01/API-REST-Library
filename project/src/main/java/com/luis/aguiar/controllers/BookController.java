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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/library/v1")
public class BookController {

    @Autowired
    private BookService service;

    @PostMapping("/books")
    public ResponseEntity<BookResponseDto> createBook(@RequestBody @Valid BookCreateDto bookCreateDto) {
        Book result = service.save(BookMapper.toBook(bookCreateDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(BookMapper.toResponseDto(result));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponseDto> findBookById(@PathVariable(name = "id") UUID uuid) {
        Book result = service.findBookById(uuid);
        return ResponseEntity.status(HttpStatus.FOUND).body(BookMapper.toResponseDto(result));
    }

    @GetMapping("/books")
    @Transactional
    public ResponseEntity<List<BookResponseDto>> findAllBooks() {
        List<Book> books = service.getAll();
        List<BookResponseDto> responseBooks = books.stream()
                .map(BookMapper::toResponseDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(responseBooks);
    }

    @GetMapping("/books/name")
    @Transactional
    public ResponseEntity<List<BookResponseDto>> findAllByName(@RequestParam String name) {
        List<Book> books = service.getAllByName(name);
        List<BookResponseDto> booksDto = books.stream()
                .map(BookMapper::toResponseDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(booksDto);
    }
}