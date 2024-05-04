package com.luis.aguiar.services;

import com.luis.aguiar.exceptions.EntityNotFoundException;
import com.luis.aguiar.models.Author;
import com.luis.aguiar.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository repository;

    public Author save(Author author) {
        return repository.save(author);
    }

    public List<Author> findAll() {
        return repository.findAll();
    }

    public Author findById(UUID uuid) {
        return repository.findById(uuid).orElseThrow(
                () -> new EntityNotFoundException("There is no author for this id.")
        );
    }

    public Author update(UUID uuid, Author author) {
        Author findAuthor = findById(uuid);
        findAuthor.setBirthDate(author.getBirthDate());
        findAuthor.setNationality(author.getNationality());
        findAuthor.setFirstName(author.getFirstName());
        findAuthor.setLastName(author.getLastName());

        return repository.save(findAuthor);
    }

    public void delete(UUID uuid) {
        Author author = findById(uuid);
        repository.delete(author);
    }
}