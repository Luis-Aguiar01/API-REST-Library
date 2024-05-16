package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.*;
import com.luis.aguiar.mappers.AuthorMapper;
import com.luis.aguiar.models.Author;
import com.luis.aguiar.services.AuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("library/v1/authors")
public class AuthorController {

    @Autowired
    private AuthorService service;

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

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> findAuthorById(@PathVariable(name = "id") UUID uuid) {
        AuthorResponseDto author = service.findById(uuid);

        addDeleteAuthorReference(author, author.getId());
        addUpdateAuthorReference(author, author.getId());
        addAssociateAuthorWithBookReference(author, author.getId());

        return ResponseEntity.status(HttpStatus.FOUND).body(author);
    }

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