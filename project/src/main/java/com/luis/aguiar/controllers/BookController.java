package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.BookCreateDto;
import com.luis.aguiar.dto.BookResponseDto;
import com.luis.aguiar.mappers.BookMapper;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/library/v1")
public class BookController {

    @Autowired
    private BookService service;

    @PostMapping("/books")
    public ResponseEntity<BookResponseDto> createBook(@RequestBody BookCreateDto bookCreateDto) {
        Book result = service.save(BookMapper.toBook(bookCreateDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(BookMapper.toResponseDto(result));
    }
}
