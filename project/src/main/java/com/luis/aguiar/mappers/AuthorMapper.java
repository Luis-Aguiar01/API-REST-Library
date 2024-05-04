package com.luis.aguiar.mappers;

import com.luis.aguiar.dto.AuthorCreateDto;
import com.luis.aguiar.dto.AuthorResponseDto;
import com.luis.aguiar.dto.UserCreateDto;
import com.luis.aguiar.models.Author;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorMapper {

    private static final ModelMapper mapper = new ModelMapper();

    public static Author toAuthor(AuthorCreateDto userDto) {
        return mapper.map(userDto, Author.class);
    }

    public static Author toAuthor(AuthorResponseDto userDto) {
        return mapper.map(userDto, Author.class);
    }

    public static AuthorResponseDto toResponseDto(Author author) {
        return mapper.map(author, AuthorResponseDto.class);
    }
}
