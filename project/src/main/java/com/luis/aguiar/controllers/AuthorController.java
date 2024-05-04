package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.AuthorCreateDto;
import com.luis.aguiar.dto.AuthorResponseDto;
import com.luis.aguiar.mappers.AuthorMapper;
import com.luis.aguiar.models.Author;
import com.luis.aguiar.services.AuthorService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("library/v1/authors")
public class AuthorController {

    @Autowired
    private AuthorService service;

    @PostMapping
    public ResponseEntity<AuthorResponseDto> createAuthor(@RequestBody @Valid AuthorCreateDto authorDto) {
        Author author = service.save(AuthorMapper.toAuthor(authorDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthorMapper.toResponseDto(author));
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<AuthorResponseDto>> findAllAuthors() {
        List<Author> authors = service.findAll();
        List<AuthorResponseDto> authorsDto = authors.stream()
                .map(AuthorMapper::toResponseDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(authorsDto);
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<AuthorResponseDto> findAuthorById(@PathVariable(name = "id") UUID uuid) {
        Author author = service.findById(uuid);
        return ResponseEntity.status(HttpStatus.FOUND).body(AuthorMapper.toResponseDto(author));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<AuthorResponseDto> updateAuthorData(@PathVariable(name = "id") UUID uuid,
                                                              @RequestBody @Valid AuthorCreateDto authorDto) {
        Author author = service.update(uuid, AuthorMapper.toAuthor(authorDto));
        return ResponseEntity.ok(AuthorMapper.toResponseDto(author));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAuthor(@PathVariable(name = "id") UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.ok("Author deleted successfully.");
    }
}