package com.luis.aguiar.repositories;

import com.luis.aguiar.enums.Status;
import com.luis.aguiar.models.Author;
import com.luis.aguiar.models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {
    @Autowired
    private BookRepository repository;
    @Autowired
    private AuthorRepository authorRepository;
    private Book book1;
    private Book book2;
    private Author author1;
    private Author author2;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        authorRepository.deleteAll();

        author1 = new Author(
                null,
                "Agatha",
                "Christie",
                LocalDate.of(1940, 10, 10),
                "British",
                new HashSet<>()
        );
        author2 = new Author(
                null,
                "John",
                "Tolkien",
                LocalDate.of(1940, 10, 10),
                "British",
                new HashSet<>()
        );

        authorRepository.save(author1);
        authorRepository.save(author2);

        book1 = new Book(
                null,
                "O Senhor dos Anéis",
                new HashSet<>(Collections.singleton(author2)),
                new HashSet<>(),
                LocalDate.of(1920, 10, 10),
                Status.AVAILABLE
        );
        book2 = new Book(
                null,
                "E Não Sobrou Nenhum",
                new HashSet<>(Collections.singleton(author1)),
                new HashSet<>(),
                LocalDate.of(1920, 10, 10),
                Status.UNAVAILABLE
        );

        repository.save(book1);
        repository.save(book2);
    }

    @Test
    @DisplayName("Should return a list of books when a valid title is passed as an argument to findByTitleContainingIgnoreCase method.")
    void shouldReturnAListOfBooks_whenAValidTitleIsPassedAsAnArgumentToFindByTitleContainingIgnoreCaseMethod() {
        var findBooks = repository.findByTitleContainingIgnoreCase("O SENHOR DOS", Pageable.unpaged());

        assertThat(findBooks).isNotNull();
        assertThat(findBooks.size()).isEqualTo(1);
        assertThat(findBooks.iterator().next().getTitle()).isEqualTo("O Senhor dos Anéis");

        findBooks = repository.findByTitleContainingIgnoreCase("E Não Sobrou Nenhum", Pageable.unpaged());

        assertThat(findBooks).isNotNull();
        assertThat(findBooks.size()).isEqualTo(1);
        assertThat(findBooks.iterator().next().getTitle()).isEqualTo("E Não Sobrou Nenhum");

        findBooks = repository.findByTitleContainingIgnoreCase("ABC", Pageable.unpaged());

        assertThat(findBooks).isNotNull();
        assertThat(findBooks.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return a list of books when author last name is passed as an argument to findByAuthorsLastName method.")
    void shouldReturnAListOfBooks_whenAuthorLastNameIsPassedAsAnArgumentToFindByAuthorsLastNameMethod() {
        var findBooks = repository.findByAuthorsLastNameContainingIgnoreCase("TOLKIEN", Pageable.unpaged());

        assertThat(findBooks).isNotNull();
        assertThat(findBooks.size()).isEqualTo(1);
        assertThat(findBooks.iterator().next().getTitle()).isEqualTo("O Senhor dos Anéis");

        findBooks = repository.findByAuthorsLastNameContainingIgnoreCase("Christie", Pageable.unpaged());

        assertThat(findBooks).isNotNull();
        assertThat(findBooks.size()).isEqualTo(1);
        assertThat(findBooks.iterator().next().getTitle()).isEqualTo("E Não Sobrou Nenhum");

        findBooks = repository.findByAuthorsLastNameContainingIgnoreCase("Tolstói", Pageable.unpaged());

        assertThat(findBooks).isNotNull();
        assertThat(findBooks.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return a list of books when a status of book is passed as an argument to findByStatus method.")
    void shouldReturnAListOfBooks_whenAStatusOfBookIsPassedAsAnArgumentToFindByStatusMethod() {
        var findBooks = repository.findByStatus(Status.AVAILABLE, Pageable.unpaged());

        assertThat(findBooks).isNotNull();
        assertThat(findBooks.size()).isEqualTo(1);
        assertThat(findBooks.iterator().next().getTitle()).isEqualTo("O Senhor dos Anéis");

        findBooks = repository.findByStatus(Status.UNAVAILABLE, Pageable.unpaged());

        assertThat(findBooks).isNotNull();
        assertThat(findBooks.size()).isEqualTo(1);
        assertThat(findBooks.iterator().next().getTitle()).isEqualTo("E Não Sobrou Nenhum");
    }
}