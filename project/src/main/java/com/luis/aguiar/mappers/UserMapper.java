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
        return mapper.map(user, UserResponseDto.class);
    }

    public static User toUser(UserResponseDto userDto) {
        return mapper.map(userDto, User.class);
    }

    public static User toUser(UserCreateDto userDto) {
        return mapper.map(userDto, User.class);
    }
}
