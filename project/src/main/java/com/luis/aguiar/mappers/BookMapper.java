package com.luis.aguiar.mappers;

import com.luis.aguiar.dto.BookCreateDto;
import com.luis.aguiar.dto.BookResponseDto;
import com.luis.aguiar.models.Book;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookMapper {

    private static final ModelMapper mapper = new ModelMapper();

    public static BookResponseDto toResponseDto(Book book) {
        return mapper.map(book, BookResponseDto.class);
    }

    public static Book toBook(BookResponseDto bookDto) {
        return mapper.map(bookDto, Book.class);
    }

    public static Book toBook(BookCreateDto bookDto) {
        return mapper.map(bookDto, Book.class);
    }
}