package com.luis.aguiar.services;

import com.luis.aguiar.dto.AuthorResponseDto;
import com.luis.aguiar.exceptions.EntityNotFoundException;
import com.luis.aguiar.mappers.AuthorMapper;
import com.luis.aguiar.models.Author;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.repositories.AuthorRepository;
import com.luis.aguiar.repositories.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    public Author save(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    public List<AuthorResponseDto> findAll() {
        List<Author> authors = authorRepository.findAll();
        List<AuthorResponseDto> authorsDto = authors.stream()
                .map(AuthorMapper::toResponseDto)
                .toList();
        return authorsDto;
    }

    @Transactional
    public AuthorResponseDto findById(UUID uuid) {
        Author author = authorRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("There is no author for this id."));
        return AuthorMapper.toResponseDto(author);
    }

    public Author update(UUID uuid, Author author) {
        Author findAuthor = authorRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("There is no author for this id."));

        findAuthor.setBirthDate(author.getBirthDate());
        findAuthor.setNationality(author.getNationality());
        findAuthor.setFirstName(author.getFirstName());
        findAuthor.setLastName(author.getLastName());

        return authorRepository.save(findAuthor);
    }

    @Transactional
    public void delete(UUID uuid) {
        Author author = authorRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("There is no author for this id."));
        authorRepository.delete(author);
    }

    @Transactional
    public void associateAuthorWithBook(UUID bookID, UUID authorID) {
        Book book = bookRepository.findById(bookID)
                .orElseThrow(() -> new EntityNotFoundException("There is no book for this id."));

        Author author = authorRepository.findById(authorID)
                .orElseThrow(() -> new EntityNotFoundException("There is no author for this id."));

        Set<Author> authors = book.getAuthors();
        authors.add(author);

        bookRepository.save(book);
    }
}