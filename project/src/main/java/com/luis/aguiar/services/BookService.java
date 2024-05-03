package com.luis.aguiar.services;

import com.luis.aguiar.dto.BookCreateDto;
import com.luis.aguiar.exceptions.UniqueDataViolationException;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private BookRepository repository;

    public Book save(Book book) {
        try {
            return repository.save(book);
        }
        catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new UniqueDataViolationException("A book with this title has already been registered");
        }
    }
}
