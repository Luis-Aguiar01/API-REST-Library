package com.luis.aguiar.services;

import com.luis.aguiar.dto.BookResponseDto;
import com.luis.aguiar.enums.Status;
import com.luis.aguiar.exceptions.*;
import com.luis.aguiar.mappers.BookMapper;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.repositories.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BookService {

    @Autowired
    private BookRepository repository;

    public Book save(Book book) {
        try {
            return repository.save(book);
        }
        catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new UniqueDataViolationException("A book with this title has already been registered.");
        }
    }

    @Transactional
    public BookResponseDto findById(UUID uuid) {
        Book book = repository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));
        return BookMapper.toResponseDto(book);
    }

    @Transactional
    public List<BookResponseDto> getAll(int page, int quantity) {
        List<Book> books = repository.findAll(PageRequest.of(page, quantity)).getContent();
        return books.stream()
                .map(BookMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public List<BookResponseDto> getAllByName(String name, int page, int quantity) {
        List<Book> books = repository.findByTitleContainingIgnoreCase(name, PageRequest.of(page, quantity));
        return books.stream()
                .map(BookMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public List<BookResponseDto> getAllByAuthor(String lastName, int page, int quantity) {
        List<Book> books = repository.findByAuthorsLastNameContainingIgnoreCase(lastName, PageRequest.of(page, quantity));
        return books.stream()
                .map(BookMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public List<BookResponseDto> getAllByStatus(Status status, int page, int quantity) {
        List<Book> books = repository.findByStatus(status, PageRequest.of(page, quantity));
        return books.stream()
                .map(BookMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public BookResponseDto update(UUID uuid, Book bookNewData) {
        Book book = repository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));

        book.setTitle(bookNewData.getTitle());
        book.setPublicationDate(bookNewData.getPublicationDate());
        book.setStatus(bookNewData.getStatus());

        return BookMapper.toResponseDto(repository.save(book));
    }

    @Transactional
    public void delete(UUID uuid) {
        Book book = repository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));
        repository.delete(book);
    }
}