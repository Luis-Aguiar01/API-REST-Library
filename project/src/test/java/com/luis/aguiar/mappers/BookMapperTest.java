package com.luis.aguiar.mappers;

import com.luis.aguiar.controllers.AuthorController;
import com.luis.aguiar.dto.BookCreateDto;
import com.luis.aguiar.dto.BookResponseDto;
import com.luis.aguiar.enums.Status;
import com.luis.aguiar.models.Author;
import com.luis.aguiar.models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;

class BookMapperTest {
    private Book book;
    private BookCreateDto createDto;
    private BookResponseDto responseDto;
    private Author author;

    @BeforeEach
    void setup() {
        createDto = new BookCreateDto(
                "O Senhor dos Anéis",
                LocalDate.of(1920, 10, 10),
                Status.AVAILABLE
        );
        book = new Book(
                UUID.randomUUID(),
                "O Senhor dos Anéis",
                new HashSet<>(),
                new HashSet<>(),
                LocalDate.of(1920, 10, 10),
                Status.AVAILABLE
        );
        responseDto = new BookResponseDto(
                UUID.randomUUID(),
                "O Senhor dos Anéis",
                LocalDate.of(1920, 10, 10),
                Status.AVAILABLE,
                new HashSet<>()
        );
        author = new Author(
                UUID.randomUUID(),
                "Agatha",
                "Christie",
                LocalDate.of(1980, 10, 20),
                "British",
                new HashSet<>()
        );
    }

    @Test
    @DisplayName("Should return a Book when a valid BookCreateDto is passed as an argument to toBook method.")
    void shouldReturnABook_whenAValidBookCreateDtoIsPassedAsAnArgumentToToBookMethod() {
        var book = BookMapper.toBook(createDto);

        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo(createDto.getTitle());
        assertThat(book.getStatus()).isEqualByComparingTo(createDto.getStatus());
        assertThat(book.getPublicationDate()).isEqualTo(createDto.getPublicationDate());
    }

    @Test
    @DisplayName("Should return a BookResponseDto when a valid Book is passed as an argument to toResponseDto method.")
    void shouldReturnABookResponseDto_whenAValidBookIsPassedAsAnArgumentToToResponseDtoMethod() {
        book.getAuthors().add(author);

        var responseDto = BookMapper.toResponseDto(book);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getTitle()).isEqualTo(book.getTitle());
        assertThat(responseDto.getAuthors()).isNotNull();
        assertThat(responseDto.getAuthors()).isNotEmpty();
        assertThat(responseDto.getAuthors().size()).isEqualTo(book.getAuthors().size());
    }

    @Test
    @DisplayName("Should return a Book when a valid BookResponseDto is passed as an argument to toBook method.")
    void shouldReturnABook_whenAValidBookResponseDtoIsPassedAsAnArgumentToToBookMethod() {
        responseDto.getAuthors().add(
                linkTo(methodOn(AuthorController.class)
                    .findAuthorById(author.getId()))
                    .withSelfRel()
                    .withType(HttpMethod.GET.name())
        );

        var book = BookMapper.toBook(responseDto);

        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo(responseDto.getTitle());
        assertThat(book.getId()).isEqualByComparingTo(responseDto.getId());
        assertThat(book.getStatus()).isEqualByComparingTo(responseDto.getStatus());
        assertThat(book.getAuthors().size()).isEqualTo(responseDto.getAuthors().size());
        assertThat(book.getPublicationDate()).isEqualTo(responseDto.getPublicationDate());
    }

    @Test
    @DisplayName("Should throw an Exception when the book passed as an argument to toResponseDto method is null.")
    void shouldThrowAnException_whenTheBookPassedAsAnArgumentToToResponseDtoMethodIsNull() {
        assertThatThrownBy(() -> BookMapper.toResponseDto(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The book can't be null.");
    }

    @Test
    @DisplayName("Should throw an Exception when the ResponseBookDto as an argument to toBook method is null.")
    void shouldThrowAnException_whenTheResponseBookDtoAsAnArgumentToToBookMethodIsNull() {
        assertThatThrownBy(() -> BookMapper.toBook((BookResponseDto) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The BookResponseDto can't be null.");
    }

    @Test
    @DisplayName("Should throw an Exception when the BookCreateDto as an argument to toBook method is null.")
    void shouldThrowAnException_whenTheCreateBookDtoAsAnArgumentToToBookMethodIsNull() {
        assertThatThrownBy(() -> BookMapper.toBook((BookCreateDto) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The BookCreateDto can't be null.");
    }
}