package com.luis.aguiar.repositories;

import com.luis.aguiar.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
}