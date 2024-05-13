package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.AuthorCreateDto;
import com.luis.aguiar.dto.AuthorResponseDto;
import com.luis.aguiar.mappers.AuthorMapper;
import com.luis.aguiar.models.Author;
import com.luis.aguiar.services.AuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("library/v1/authors")
public class AuthorController {

    @Autowired
    private AuthorService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> createAuthor(@RequestBody @Valid AuthorCreateDto authorDto) {
        Author author = service.save(AuthorMapper.toAuthor(authorDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthorMapper.toResponseDto(author));
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseDto>> findAllAuthors() {
        List<AuthorResponseDto> authors = service.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(authors);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> findAuthorById(@PathVariable(name = "id") UUID uuid) {
        AuthorResponseDto author = service.findById(uuid);
        return ResponseEntity.status(HttpStatus.FOUND).body(author);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> updateAuthorData(@PathVariable(name = "id") UUID uuid,
                                                              @RequestBody @Valid AuthorCreateDto authorDto) {
        Author author = service.update(uuid, AuthorMapper.toAuthor(authorDto));
        return ResponseEntity.ok(AuthorMapper.toResponseDto(author));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteAuthor(@PathVariable(name = "id") UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.ok("Author deleted successfully.");
    }

    @PostMapping("/{authorID}/{bookID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> associateAuthorWithBook(@PathVariable(name = "bookID") UUID bookID,
                                                        @PathVariable(name = "authorID") UUID authorID) {
        service.associateAuthorWithBook(bookID, authorID);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}