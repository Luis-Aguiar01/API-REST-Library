package com.luis.aguiar.mappers;

import com.luis.aguiar.dto.AuthorCreateDto;
import com.luis.aguiar.dto.AuthorResponseDto;
import com.luis.aguiar.dto.BookResponseDto;
import com.luis.aguiar.enums.Status;
import com.luis.aguiar.models.Author;
import com.luis.aguiar.models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class AuthorMapperTest {

    private AuthorResponseDto responseDto;
    private Author author;
    private AuthorCreateDto createDto;

    @BeforeEach
    void setup() {
        author = new Author(
                UUID.randomUUID(),
                "Agatha",
                "Christie",
                LocalDate.of(1980, 10, 20),
                "British",
                new HashSet<>()
        );
        responseDto = new AuthorResponseDto(
                UUID.randomUUID(),
                "Agatha",
                "Christie",
                LocalDate.of(1980, 10, 10),
                "British",
                new HashSet<>()
        );
        createDto = new AuthorCreateDto(
                "Agatha",
                "Christie",
                LocalDate.of(1980, 10, 10),
                "British"
        );
    }

    @Test
    @DisplayName("Should return an Author Response Dto when a valid author is passed as an argument.")
    void shouldReturnAnAuthorResponseDto_whenAValidAuthorIsPassedAsAnArgument() {
        author.getBooks().add(
                new Book(
                    UUID.randomUUID(),
                    "O Senhor dos Anéis",
                    new HashSet<>(),
                    new HashSet<>(),
                    LocalDate.of(1920, 10, 10),
                    Status.AVAILABLE
        ));

        AuthorResponseDto dto = AuthorMapper.toResponseDto(author);

        assertThat(dto).isNotNull();
        assertThat(author.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(author.getLastName()).isEqualTo(dto.getLastName());
        assertThat(author.getId()).isEqualByComparingTo(dto.getId());
        assertThat(author.getNationality()).isEqualTo(dto.getNationality());
        assertThat(author.getBirthDate()).isEqualTo(dto.getBirthDate());
        assertThat(author.getBooks()).isNotNull();
        assertThat(author.getBooks()).isNotEmpty();
        assertThat(author.getBooks().iterator().next().getTitle()).isEqualTo("O Senhor dos Anéis");
    }

    @Test
    @DisplayName("Should return an Author when a valid Author Response Dto is passed as an argument.")
    void shouldReturnAnAuthor_whenAnValidAuthorResponseDtoIsPassedAsAnArgument() {
        responseDto.getBooks().add(
                new BookResponseDto(
                    UUID.randomUUID(),
                    "O Senhor dos Anéis",
                    LocalDate.of(1920, 10, 10),
                    Status.AVAILABLE,
                    new HashSet<>()
                )
        );

        Author author = AuthorMapper.toAuthor(responseDto);

        assertThat(author).isNotNull();
        assertThat(author.getFirstName()).isEqualTo(responseDto.getFirstName());
        assertThat(author.getLastName()).isEqualTo(responseDto.getLastName());
        assertThat(author.getId()).isEqualByComparingTo(responseDto.getId());
        assertThat(author.getNationality()).isEqualTo(responseDto.getNationality());
        assertThat(author.getBooks()).isNotNull();
        assertThat(author.getBooks()).isNotEmpty();
        assertThat(author.getBooks().iterator().next().getTitle()).isEqualTo("O Senhor dos Anéis");
    }

    @Test
    @DisplayName("Should return an author when a valid Author Create Dto is passed as an argument.")
    void shouldReturnAnAuthor_whenAValidAuthorCreateDtoIsPassedAsAnArgument() {
        Author author = AuthorMapper.toAuthor(createDto);

        assertThat(author).isNotNull();
        assertThat(author.getFirstName()).isEqualTo(createDto.getFirstName());
        assertThat(author.getLastName()).isEqualTo(createDto.getLastName());
        assertThat(author.getBirthDate()).isEqualTo(createDto.getBirthDate());
        assertThat(author.getNationality()).isEqualTo(createDto.getNationality());
    }

    @Test
    @DisplayName("Should throw an exception when a null value is passed as an argument to toResponseDto method.")
    void shouldThrowAnException_whenANullValueIsPassedAsAnArgumentToToResponseDtoMethod() {
        assertThatThrownBy(() -> AuthorMapper.toResponseDto(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Author can't be null.");
    }

    @Test
    @DisplayName("Should throw an exception when a null AuthorResponseDto is passed as an argument to toAuthor method.")
    void shouldThrowAnException_whenANullAuthorResponseDtoIsPassedAsAnArgumentToToAuthorMethod() {
        assertThatThrownBy(() -> AuthorMapper.toAuthor((AuthorResponseDto) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("AuthorResponseDto can't be null.");
    }

    @Test
    @DisplayName("Should throw an exception when a null AuthorCreateDto is passed as an argument to toAuthorMethod.")
    void shouldThrowAnException_whenANullAuthorCreateDtoIsPassedAsAnArgumentToToAuthorMethod() {
        assertThatThrownBy(() -> AuthorMapper.toAuthor((AuthorCreateDto) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("AuthorCreateDto can't be null.");
    }
}