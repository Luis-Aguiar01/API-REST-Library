package com.luis.aguiar.services;

import com.luis.aguiar.exceptions.EntityNotFoundException;
import com.luis.aguiar.exceptions.UniqueDataViolationException;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public Book findById(UUID uuid) {
        return repository.findById(uuid).orElseThrow(
                () -> new EntityNotFoundException("Book not found.")
        );
    }

    public List<Book> getAll() {
        return repository.findAll();
    }

    public List<Book> getAllByName(String name) {
        return repository.findByTitleContainingIgnoreCase(name);
    }

    public List<Book> getAllByAuthor(String firstName, String lastName) {
        return repository.findByAuthorsFirstNameAndAuthorsLastName(firstName, lastName);
    }

    public List<Book> getAllByStatus(Book.Status status) {
        return repository.findByStatus(status);
    }

    public Book update(UUID uuid, Book bookNewData) {
        Book findBook = findById(uuid);
        findBook.setTitle(bookNewData.getTitle());
        findBook.setPublicationDate(bookNewData.getPublicationDate());
        findBook.setStatus(bookNewData.getStatus());

        return repository.save(findBook);
    }

    public void delete(UUID uuid) {
        Book book = findById(uuid);
        repository.delete(book);
    }
}