package com.luis.aguiar.mappers;

import com.luis.aguiar.dto.AuthorResponseDto;
import com.luis.aguiar.dto.BookCreateDto;
import com.luis.aguiar.dto.BookResponseDto;
import com.luis.aguiar.models.Book;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookMapper {

    private static final ModelMapper mapper = new ModelMapper();

    public static BookResponseDto toResponseDto(Book book) {
        BookResponseDto dto = new BookResponseDto();
        dto.setTitle(book.getTitle());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setStatus(book.getStatus());
        dto.setId(book.getId());

        Set<AuthorResponseDto> authors = book.getAuthors().stream()
                .map(AuthorMapper::toResponseDto)
                .collect(Collectors.toSet());

        dto.setAuthors(authors);
        return dto;
    }

    public static Book toBook(BookResponseDto bookDto) {
        return mapper.map(bookDto, Book.class);
    }

    public static Book toBook(BookCreateDto bookDto) {
        return mapper.map(bookDto, Book.class);
    }
}