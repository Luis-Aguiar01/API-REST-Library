package com.luis.aguiar.mappers;

import com.luis.aguiar.dto.UserCreateDto;
import com.luis.aguiar.dto.UserResponseDto;
import com.luis.aguiar.enums.Role;
import com.luis.aguiar.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserMapperTest {

    @Test
    @DisplayName("Should return an User when a valid UserCreateDto is passed as an argument to toUser method.")
    void shouldReturnAnUser_whenAValidUserCreateDtoIsPassedAsAnArgumentToToUserMethod() {
        UserCreateDto createDto = new UserCreateDto(
            "Luis",
            "Aguiar",
            "luis@gmail.com",
            "123456",
            LocalDate.of(2000, 10, 10)
        );

        User user = UserMapper.toUser(createDto);

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(createDto.getEmail());
        assertThat(user.getFirstName()).isEqualTo(createDto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(createDto.getLastName());
        assertThat(user.getBirthDate()).isEqualTo(createDto.getBirthDate());
    }

    @Test
    @DisplayName("Should return an UserResponseDto when a valid user is passed as an argument to toResponseDto method.")
    void shouldReturnAnUserResponseDto_whenAValidUserIsPassedAsAnArgumentToToResponseDtoMethod() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("Luis")
                .lastName("Aguiar")
                .email("luis@gmail.com")
                .password("123456")
                .birthDate(LocalDate.of(2000, 10, 10))
                .hasBookOnLoan(false)
                .role(Role.USER)
                .loans(new HashSet<>())
                .build();

        UserResponseDto responseDto = UserMapper.toResponseDto(user);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(responseDto.getBirthDate()).isEqualTo(user.getBirthDate());
        assertThat(responseDto.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(responseDto.getLastName()).isEqualTo(user.getLastName());
    }

    @Test
    @DisplayName("Should throw an Exception when the value passed as an argument to toUser method is null.")
    void shouldThrowAnException_whenTheValuePassedAsAnArgumentToToUserMethodIsNull() {
        assertThatThrownBy(() -> UserMapper.toUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The UserCreateDto can't be null.");
    }

    @Test
    @DisplayName("Should throw an Exception when the value passed as an argument to toResponseDto method is null.")
    void shouldThrowAnException_whenTheValuePassedAsAnArgumentToToResponseDtoMethodIsNull() {
        assertThatThrownBy(() -> UserMapper.toResponseDto(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The User can't be null.");
    }
}