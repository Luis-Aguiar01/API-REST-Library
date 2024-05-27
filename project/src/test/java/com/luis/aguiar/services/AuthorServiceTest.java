package com.luis.aguiar.services;

import com.luis.aguiar.dto.AuthorResponseDto;
import com.luis.aguiar.enums.Status;
import com.luis.aguiar.exceptions.EntityNotFoundException;
import com.luis.aguiar.models.Author;
import com.luis.aguiar.models.Book;
import com.luis.aguiar.repositories.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.time.*;
import java.util.*;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    @Mock
    AuthorRepository authorRepository;
    @Mock
    BookRepository bookRepository;
    @InjectMocks
    AuthorService service;
    private Author author1;
    private Author author2;
    private Book book;

    @BeforeEach
    void setup() {
        author1 = new Author(
                UUID.randomUUID(),
                "Liev",
                "Tolstói",
                LocalDate.of(1828, 9, 9),
                "Russian",
                new HashSet<>()
        );
        author2 = new Author(
                UUID.randomUUID(),
                "Alexandre",
                "Dumas",
                LocalDate.of(1802, 6, 24),
                "French",
                new HashSet<>()
        );
        book = new Book(
                UUID.randomUUID(),
                "Guerra e Paz",
                new HashSet<>(),
                new HashSet<>(),
                LocalDate.of(1867, 10, 10),
                Status.AVAILABLE
        );
    }

    @Test
    @DisplayName("Should return an Author when an Author is passed as an argument to save method.")
    void shouldReturnAnAuthor_whenAnAuthorIsPassedAsAnArgumentToSaveMethod() {
        // given
        given(authorRepository.save(any(Author.class))).willReturn(author1);

        // when
        Author returnedAuthor = service.save(author1);

        // then
        assertThat(returnedAuthor).isNotNull();
        assertThat(returnedAuthor.getId()).isEqualTo(author1.getId());
        assertThat(returnedAuthor.getFirstName()).isEqualTo(author1.getFirstName());
        assertThat(returnedAuthor.getLastName()).isEqualTo(author1.getLastName());

        // verify
        then(authorRepository).should(times(1)).save(any(Author.class));
        then(authorRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should return a list of AuthorResponseDto when the findAll method is called.")
    void shouldReturnAListOfAuthorResponseDto_whenTheFindAllMethodIsCalled() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<Author> page = new PageImpl<>(List.of(author1, author2));
        given(authorRepository.findAll(pageRequest)).willReturn(page);

        // when
        List<AuthorResponseDto> authors = service.findAll(0, 2);

        // then
        assertThat(authors).isNotNull();
        assertThat(authors).isNotEmpty();
        assertThat(authors.size()).isEqualTo(2);
        assertThat(authors.get(0).getFirstName()).isEqualTo(author1.getFirstName());
        assertThat(authors.get(0).getLastName()).isEqualTo(author1.getLastName());
        assertThat(authors.get(1).getFirstName()).isEqualTo(author2.getFirstName());
        assertThat(authors.get(1).getLastName()).isEqualTo(author2.getLastName());

        // verify
        then(authorRepository).should(times(1)).findAll(pageRequest);
        then(authorRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should return an Author when an existing ID is passed as an argument to findById method.")
    void shouldReturnAnAuthor_whenAnExistingIdIsPassedAsAnArgumentToFindByIdMethod() {
        // given
        given(authorRepository.findById(any(UUID.class))).willReturn(Optional.of(author1));

        // when
        AuthorResponseDto author = service.findById(UUID.randomUUID());

        // then
        assertThat(author).isNotNull();
        assertThat(author.getFirstName()).isEqualTo("Liev");
        assertThat(author.getLastName()).isEqualTo("Tolstói");
        assertThat(author.getNationality()).isEqualTo("Russian");

        // verify
        then(authorRepository).should(times(1)).findById(any(UUID.class));
        then(authorRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should thrown an exception when a non-existing ID is passed as an argument to findById method.")
    void shouldThrowAnException_whenANonExistingIdIsPassedAsAnArgumentToFindByIdMethod() {
        // given
        given(authorRepository.findById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("There is no author for this id."));

        // when & then
        assertThatThrownBy(() -> service.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("There is no author for this id.");

        // verify
        then(authorRepository).should(times(1)).findById(any(UUID.class));
        then(authorRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should return an author with updated data " +
            "when an existing ID and an Author is passed as an argument to update method."
    )
    void shouldReturnAnAuthorWithUpdatedData_whenAnExistingIdAndAnAuthorIsPassedAsAnArgumentToUpdateMethod() {
        // given
        given(authorRepository.findById(any(UUID.class))).willReturn(Optional.of(author1));
        given(authorRepository.save(any(Author.class))).willReturn(author2);

        // when
        AuthorResponseDto updatedAuthor = service.update(UUID.randomUUID(), author2);

        // then
        assertThat(updatedAuthor).isNotNull();
        assertThat(updatedAuthor.getFirstName()).isEqualTo(author2.getFirstName());
        assertThat(updatedAuthor.getLastName()).isEqualTo(author2.getLastName());
        assertThat(updatedAuthor.getBirthDate()).isEqualTo(author2.getBirthDate());
        assertThat(updatedAuthor.getNationality()).isEqualTo(author2.getNationality());

        // verify
        then(authorRepository).should(times(1)).findById(any(UUID.class));
        then(authorRepository).should(times(1)).save(any(Author.class));
        then(authorRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an exception when a non-existing ID is passed as an argument to update method.")
    void shouldThrowAnException_whenANonExistingIdIsPassedAsAnArgumentToUpdateMethod() {
        // given
        given(authorRepository.findById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("There is no author for this id."));

        // when & then
        assertThatThrownBy(() -> service.update(UUID.randomUUID(), author1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("There is no author for this id.");

        // verify
        then(authorRepository).should(times(1)).findById(any(UUID.class));
        then(authorRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should delete an Author when an existing ID is passed as an argument to delete method.")
    void shouldDeleteAnAuthor_whenAnExistingIdIsPassedAsAnArgumentToDeleteMethod() {
        // given
        given(authorRepository.findById(any(UUID.class))).willReturn(Optional.of(author1));

        // when
        service.delete(UUID.randomUUID());

        // then
        then(authorRepository).should(times(1)).findById(any(UUID.class));
        then(authorRepository).should(times(1)).delete(any(Author.class));
        then(authorRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an exception when a non-existing ID is passed as an argument to delete method.")
    void shouldThrowAnException_whenANonExistingIdIsPassedAsAnArgumentToDeleteMethod() {
        // given
        given(authorRepository.findById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("There is no author for this id."));

        // when & then
        assertThatThrownBy(() -> service.delete(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("There is no author for this id.");

        then(authorRepository).should(times(1)).findById(any(UUID.class));
        then(authorRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should associate an Author with a Book " +
            "when a existing book id and author id are passed as an argument to associateAuthorWithBook method."
    )
    void shouldAssociateAnAuthorWithABook_whenAExistingBookIdAndAnAuthorIdArePassedAsAnArgumentToAssociateAuthorWithBookMethod() {
        // given
        given(authorRepository.findById(any(UUID.class))).willReturn(Optional.of(author1));
        given(bookRepository.findById(any(UUID.class))).willReturn(Optional.of(book));

        // when
        service.associateAuthorWithBook(UUID.randomUUID(), UUID.randomUUID());

        // then
        assertThat(book.getAuthors()).isNotEmpty();
        assertThat(book.getAuthors().size()).isEqualTo(1);
        assertThat(book.getAuthors().iterator().next().getLastName()).isEqualTo("Tolstói");

        // verify
        then(authorRepository).should(times(1)).findById(any(UUID.class));
        then(authorRepository).shouldHaveNoMoreInteractions();
        then(bookRepository).should(times(1)).findById(any(UUID.class));
        then(bookRepository).should(times(1)).save(any(Book.class));
        then(bookRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should throw an exception " +
            "when a non-existing author id is passed as an argument to associateAuthorWithBook method."
    )
    void shouldThrowAnException_whenANonExistingAuthorIdIsPassedAsAnArgumentToAssociateAuthorWithBookMethod() {
        // given
        given(authorRepository.findById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("There is no author for this id."));
        given(bookRepository.findById(any(UUID.class))).willReturn(Optional.of(book));

        // when & then
        assertThatThrownBy(() -> service.associateAuthorWithBook(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("There is no author for this id.");

        // verify
        then(authorRepository).should(times(1)).findById(any(UUID.class));
        then(authorRepository).shouldHaveNoMoreInteractions();
        then(bookRepository).should(times(1)).findById(any(UUID.class));
        then(bookRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should throw an exception " +
            "when a non-existing book id is passed as an argument to associateAuthorWithBook method."
    )
    void shouldThrowAnException_whenANonExistingBookIdIsPassedAsAnArgumentToAssociateAuthorWithBookMethod() {
        // given
        given(bookRepository.findById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("There is no book for this id."));

        // when & then
        assertThatThrownBy(() -> service.associateAuthorWithBook(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("There is no book for this id.");

        // verify
        then(authorRepository).shouldHaveNoInteractions();
        then(bookRepository).should(times(1)).findById(any(UUID.class));
        then(bookRepository).shouldHaveNoMoreInteractions();
    }
}