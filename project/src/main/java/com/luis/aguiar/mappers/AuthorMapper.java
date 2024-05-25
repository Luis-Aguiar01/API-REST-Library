package com.luis.aguiar.mappers;

import com.luis.aguiar.dto.AuthorCreateDto;
import com.luis.aguiar.dto.AuthorResponseDto;
import com.luis.aguiar.models.Author;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorMapper {

    private static final ModelMapper mapper = new ModelMapper();

    public static Author toAuthor(AuthorCreateDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("AuthorCreateDto can't be null.");
        }
        return mapper.map(userDto, Author.class);
    }

    public static Author toAuthor(AuthorResponseDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("AuthorResponseDto can't be null.");
        }
        return mapper.map(userDto, Author.class);
    }

    public static AuthorResponseDto toResponseDto(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author can't be null.");
        }
        return mapper.map(author, AuthorResponseDto.class);
    }
}