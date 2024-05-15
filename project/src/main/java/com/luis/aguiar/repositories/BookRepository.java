package com.luis.aguiar.repositories;

import com.luis.aguiar.enums.Status;
import com.luis.aguiar.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    List<Book> findByTitleContainingIgnoreCase(String name);
    List<Book> findByAuthorsLastNameContainingIgnoreCase(String lastName);
    List<Book> findByStatus(Status status);
}