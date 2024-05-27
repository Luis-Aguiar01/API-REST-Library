package com.luis.aguiar.services;

import com.luis.aguiar.dto.BookResponseDto;
import com.luis.aguiar.enums.Status;
import com.luis.aguiar.exceptions.EntityNotFoundException;
import com.luis.aguiar.exceptions.UniqueDataViolationException;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @InjectMocks
    private BookService service;
    @Mock
    private BookRepository repository;
    private Book book;
    private Book book2;
    private UUID id;
    private UUID id2;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID();
        id2 = UUID.randomUUID();
        book = new Book(
                id,
                "O Senhor dos Anéis",
                new HashSet<>(),
                new HashSet<>(),
                LocalDate.of(1920, 10, 10),
                Status.AVAILABLE
        );
        book2 = new Book(
                id2,
                "Guerra e Paz",
                new HashSet<>(),
                new HashSet<>(),
                LocalDate.of(1920, 10, 10),
                Status.UNAVAILABLE
        );
    }

    @Test
    @DisplayName("Should return a saved Book when a Book is passed as an argument to save method.")
    void shouldReturnASavedBook_whenABookIsPassedAsAnArgumentToSaveMethod() {
        // given
        given(repository.save(any(Book.class))).willReturn(book);

        // when
        Book returnedBook = service.save(book);

        // then
        assertThat(returnedBook).isNotNull();
        assertThat(returnedBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(returnedBook.getPublicationDate()).isEqualTo(book.getPublicationDate());

        // verify
        then(repository).should(times(1)).save(any(Book.class));
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should throw an Exception " +
            "when a book with an already registered name is passed as an argument to the save method."
    )
    void shouldThrowAnException_WhenABookWithAnAlreadyRegisteredNameIsPassedAsAnArgumentToTheSaveMethod() {
        // given
        given(repository.save(any(Book.class)))
                .willThrow(new org.springframework.dao.DataIntegrityViolationException("Data integrity violation"));

        // when & then
        assertThatThrownBy(() -> service.save(book))
                .isInstanceOf(UniqueDataViolationException.class)
                .hasMessage("A book with this title has already been registered.");

        // verify
        then(repository).should(times(1)).save(any(Book.class));
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should return a BookResponseDto when existing id is passed as an argument to findById method.")
    void shouldReturnABookResponseDto_whenExistingIdIsPassedAsAnArgumentToFindByIdMethod() {
        // given
        given(repository.findById(any(UUID.class))).willReturn(Optional.of(book));

        // when
        var bookResponseDto = service.findById(id);

        // then
        assertThat(bookResponseDto).isNotNull();
        assertThat(bookResponseDto.getTitle()).isEqualTo(book.getTitle());
        assertThat(bookResponseDto.getId()).isEqualTo(book.getId());

        // verify
        then(repository).should(times(1)).findById(any(UUID.class));
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an Exception when a non-existing id is passed as an argument to findById method.")
    void shouldThrowAnException_whenANonExistingIdIsPassedAsAnArgumentToFindByIdMethod() {
        // given
        given(repository.findById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("Book not found."));

        // when & then
        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Book not found.");

        // verify
        then(repository).should(times(1)).findById(any(UUID.class));
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should return a list of BookResponseDto when the getAll method is called.")
    void shouldReturnAListOfBookResponseDto_whenTheFindAllMethodIsCalled() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Book> page = new PageImpl<>(List.of(book, book2));
        given(repository.findAll(pageRequest)).willReturn(page);

        // when
        List<BookResponseDto> books = service.getAll(0, 5);

        // then
        assertThat(books).isNotNull();
        assertThat(books.size()).isEqualTo(2);
        assertThat(books.get(0).getTitle()).isEqualTo(book.getTitle());
        assertThat(books.get(1).getTitle()).isEqualTo(book2.getTitle());

        // verify
        then(repository).should(times(1)).findAll(pageRequest);
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should return a list of BookResponseDto " +
            "when a name of book is passed as an argument to getAllByName method."
    )
    void shouldReturnAListOfBookResponseDto_whenANameOfBookIsPassedAsAnArgumentToGetAllByNameMethod() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 2);
        given(repository.findByTitleContainingIgnoreCase("O Senhor dos Anéis", pageRequest))
                .willReturn(List.of(book));

        // when
        List<BookResponseDto> books = service.getAllByName("O Senhor dos Anéis", 0, 2);

        // then
        assertThat(books).isNotNull();
        assertThat(books.get(0).getTitle()).isEqualTo(book.getTitle());
        assertThat(books.get(0)).isInstanceOf(BookResponseDto.class);
        assertThat(books.get(0).getId()).isEqualTo(book.getId());
        assertThat(books.size()).isEqualTo(1);

        // verify
        then(repository).should(times(1))
                .findByTitleContainingIgnoreCase("O Senhor dos Anéis", pageRequest);
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should return a list of BookResponseDto " +
            "when a last name of author is passed as an argument to getAllByAuthor method."
    )
    void shouldReturnAListOfBookResponseDto_whenALastNameOfAuthorIsPassedAsAnArgumentToGetAllByAuthorMethod() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 2);
        given(repository.findByAuthorsLastNameContainingIgnoreCase("Tolkien", pageRequest))
                .willReturn(List.of(book));

        // when
        List<BookResponseDto> books = service.getAllByAuthor("Tolkien", 0, 2);

        // then
        assertThat(books).isNotNull();
        assertThat(books.size()).isEqualTo(1);
        assertThat(books.get(0)).isInstanceOf(BookResponseDto.class);
        assertThat(books.get(0).getTitle()).isEqualTo(book.getTitle());
        assertThat(books.get(0).getId()).isEqualTo(book.getId());

        // verify
        then(repository).should(times(1))
                .findByAuthorsLastNameContainingIgnoreCase("Tolkien", pageRequest);
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should return a list of BookResponseDto " +
            "when a book status is passed as an argument to getAllByStatus method."
    )
    void shouldReturnAListOfBookResponseDto_whenABookStatusIsPassedAsAnArgumentToGetAllByStatusMethod() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 2);
        given(repository.findByStatus(Status.AVAILABLE, pageRequest)).willReturn(List.of(book));

        // when
        List<BookResponseDto> books = service.getAllByStatus(Status.AVAILABLE, 0, 2);

        // then
        assertThat(books).isNotNull();
        assertThat(books.size()).isEqualTo(1);
        assertThat(books.get(0)).isInstanceOf(BookResponseDto.class);
        assertThat(books.get(0).getTitle()).isEqualTo(book.getTitle());
        assertThat(books.get(0).getId()).isEqualByComparingTo(book.getId());

        // verify
        then(repository).should(times(1)).findByStatus(Status.AVAILABLE, pageRequest);
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should return a BookResponseDto " +
            "when an ID and a book with new date is passed as an argument to update method."
    )
    void shouldReturnABookResponseDto_whenAnIdAndABookWithNewDataIsPassedAsAnArgumentToUpdateMethod() {
        // given
        given(repository.findById(any(UUID.class))).willReturn(Optional.of(book));
        given(repository.save(any(Book.class))).willReturn(book2);

        // when
        BookResponseDto book = service.update(id, book2);

        // then
        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo(book2.getTitle());
        assertThat(book.getPublicationDate()).isEqualTo(book2.getPublicationDate());

        // verify
        then(repository).should(times(1)).findById(any(UUID.class));
        then(repository).should(times(1)).save(any(Book.class));
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should thrown a Exception when a non-existing id is passed as an argument to update method.")
    void shouldThrowAException_whenANonExistingIdIsPassedAsAnArgumentToUpdateMethod() {
        // given
        given(repository.findById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("Book not found."));

        // when & then
        assertThatThrownBy(() -> service.update(id, book2))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Book not found.");

        // verify
        then(repository).should(times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should delete a Book when a existing ID is passed as an argument to delete method.")
    void shouldDeleteABook_whenAExistingIdIsPassedAsAnArgumentToDeleteMethod() {
        // given
        given(repository.findById(any(UUID.class))).willReturn(Optional.of(book));

        // when
        service.delete(id);

        // then
        then(repository).should(times(1)).findById(any(UUID.class));
        then(repository).should(times(1)).delete(any(Book.class));
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an Exception when a non-existing is passed as an argument to delete method.")
    void shouldThrownAnException_whenANonExistingIdIsPassedAsAnArgumentToDeleteMethod() {
        // given
        given(repository.findById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("Book not found."));

        // when & then
        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Book not found.");

        then(repository).should(times(1)).findById(any(UUID.class));
        then(repository).should(times(0)).delete(any(Book.class));
        then(repository).shouldHaveNoMoreInteractions();
    }
}