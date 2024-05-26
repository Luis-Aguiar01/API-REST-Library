package com.luis.aguiar.mappers;

import com.luis.aguiar.dto.UserCreateDto;
import com.luis.aguiar.dto.UserResponseDto;
import com.luis.aguiar.models.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    private static final ModelMapper mapper = new ModelMapper();

    public static UserResponseDto toResponseDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("The User can't be null.");
        }
        return mapper.map(user, UserResponseDto.class);
    }

    public static User toUser(UserCreateDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("The UserCreateDto can't be null.");
        }
        return mapper.map(userDto, User.class);
    }
}