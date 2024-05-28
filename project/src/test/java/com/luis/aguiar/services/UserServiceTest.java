package com.luis.aguiar.services;

import com.luis.aguiar.dto.UserCreateDto;
import com.luis.aguiar.enums.Role;
import com.luis.aguiar.exceptions.EntityNotFoundException;
import com.luis.aguiar.exceptions.UniqueDataViolationException;
import com.luis.aguiar.models.User;
import com.luis.aguiar.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private UserService service;
    private UserCreateDto createDto;
    private User user;
    private User user2;

    @BeforeEach
    void setup() {
        createDto = new UserCreateDto(
                "Luis",
                "Aguiar",
                "luis@gmail.com",
                "123456",
                LocalDate.of(2000, 10, 10)
        );
        user = User.builder()
                .id(UUID.randomUUID())
                .firstName("Luis")
                .lastName("Aguiar")
                .email("luis@gmail.com")
                .password("e10adc3949ba59abbe56e057f20f883e")
                .birthDate(LocalDate.of(2000, 10, 10))
                .hasBookOnLoan(false)
                .role(Role.USER)
                .loans(new HashSet<>())
                .build();
        user2 = User.builder()
                .id(UUID.randomUUID())
                .firstName("João")
                .lastName("Pedro")
                .email("joao@gmail.com")
                .password("e10adc3949ba59abbe56e057f20f883e")
                .birthDate(LocalDate.of(2000, 10, 10))
                .hasBookOnLoan(false)
                .role(Role.USER)
                .loans(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Should return an User when a valid UserCreateDto is passed as an argument to save method.")
    void shouldReturnAnUser_whenAValidUserCreateDtoIsPassedAsAnArgumentToSaveMethod() {
        // given
        given(repository.save(any(User.class))).willReturn(user);
        given(encoder.encode(any(String.class))).willReturn("e10adc3949ba59abbe56e057f20f883e");

        // when
        User returnedUser = service.save(createDto);

        // then
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser.getPassword()).isEqualTo("e10adc3949ba59abbe56e057f20f883e");
        assertThat(returnedUser.getFirstName()).isEqualTo("Luis");
        assertThat(returnedUser.getLastName()).isEqualTo("Aguiar");
        assertThat(returnedUser.getEmail()).isEqualTo("luis@gmail.com");

        // verify
        then(repository).should(times(1)).save(any(User.class));
        then(encoder).should(times(1)).encode(any(String.class));
        then(repository).shouldHaveNoMoreInteractions();
        then(encoder).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an exception when an e-mail already registered is passed as an argument to save method.")
    void shouldThrowAnException_whenAnEmailAlreadyRegisteredIsPassedAsAnArgumentToSaveMethod() {
        // given
        given(repository.save(any(User.class)))
                .willThrow(org.springframework.dao.DataIntegrityViolationException.class);

        // when
        assertThatThrownBy(() -> service.save(createDto))
                .isInstanceOf(UniqueDataViolationException.class)
                .hasMessage("A user with this email has already been registered.");

        // verify
        then(repository).should(times(1)).save(any(User.class));
        then(encoder).should(times(1)).encode(any(String.class));
        then(repository).shouldHaveNoMoreInteractions();
        then(encoder).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should return an User when an existing ID is passed as an argument to findById method.")
    void shouldReturnAnUser_whenAnExistingIdIsPassedAsAnArgumentToFindByIdMethod() {
        // given
        given(repository.findById(any(UUID.class))).willReturn(Optional.of(user));

        // when
        User returnedUser = service.findById(UUID.randomUUID());

        // then
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser.getFirstName()).isEqualTo("Luis");
        assertThat(returnedUser.getLastName()).isEqualTo("Aguiar");
        assertThat(returnedUser.getEmail()).isEqualTo("luis@gmail.com");

        // verify
        then(repository).should(times(1)).findById(any(UUID.class));
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an exception when a non-existing ID is passed as an argument to findById method.")
    void shouldThrowAnException_whenANonExistingIdIsPassedAsAnArgumentToFindByIdMethod() {
        // given
        given(repository.findById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("No user with this ID found."));

        // when & then
        assertThatThrownBy(() -> service.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No user with this ID found.");

        then(repository).should(times(1)).findById(any(UUID.class));
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should return a list of User when the findAll method is called.")
    void shouldReturnAListOfUser_whenTheFindAllMethodIsCalled() {
        // given
        PageRequest pageRequest = PageRequest.of(0,2);
        Page<User> page = new PageImpl<>(List.of(user, user2));
        given(repository.findAll(pageRequest)).willReturn(page);

        // when
        List<User> users = service.findAll(0, 2);

        // then
        assertThat(users).isNotNull();
        assertThat(users).isNotEmpty();
        assertThat(users.get(0).getFirstName()).isEqualTo("Luis");
        assertThat(users.get(0).getLastName()).isEqualTo("Aguiar");
        assertThat(users.get(1).getFirstName()).isEqualTo("João");
        assertThat(users.get(1).getLastName()).isEqualTo("Pedro");

        then(repository).should(times(1)).findAll(pageRequest);
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName(
            "Should return an User with updated data " +
            "when a valid user email and user data are passed as an argument to updated method."
    )
    void shouldReturnAnUserWithUpdatedData_whenAValidUserEmailAndUserDataArePassedAsAnArgumentToUpdateMethod() {
        // given
        given(repository.findByEmail(any(String.class))).willReturn(Optional.of(user));
        given(repository.save(any(User.class))).willReturn(user2);
        given(encoder.encode(any(String.class))).willReturn("e10adc3949ba59abbe56e057f20f883e");

        // when
        User updatedUser = service.update("luis@gmail.com", user2);

        // then
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFirstName()).isEqualTo(user2.getFirstName());
        assertThat(updatedUser.getLastName()).isEqualTo(user2.getLastName());
        assertThat(updatedUser.getEmail()).isEqualTo(user2.getEmail());

        then(repository).should(times(1)).save(any(User.class));
        then(repository).should(times(1)).findByEmail(any(String.class));
        then(encoder).should(times(1)).encode(any(String.class));
        then(encoder).shouldHaveNoMoreInteractions();
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should thrown an exception when a non-existing email is passed as an argument to update method.")
    void shouldThrowAnException_whenANonExistingEmailIsPassedAsAnArgumentToUpdateMethod() {
        // given
        given(repository.findByEmail(any(String.class)))
                .willThrow(new EntityNotFoundException("No user with this email found."));

        // when & then
        assertThatThrownBy(() -> service.update("luis@gmail.com", user2))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No user with this email found.");

        // verify
        then(repository).should(times(1)).findByEmail(any(String.class));
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should delete an User when an existing email is passed as an argument to delete method.")
    void shouldDeleteAnUser_whenAnExistingEmailIsPassedAsAnArgumentToDeleteMethod() {
        // given
        given(repository.findByEmail(any(String.class))).willReturn(Optional.of(user));

        // when
        service.delete("luis@gmail.com");

        // then
        then(repository).should(times(1)).findByEmail(any(String.class));
        then(repository).should(times(1)).delete(any(User.class));
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw an exception when a non-existing email is passed as an argument to delete method.")
    void shouldThrowAnException_whenANonExistingEmailIsPassedAsAnArgumentToDeleteMethod() {
        // given
        given(repository.findByEmail(any(String.class)))
                .willThrow(new EntityNotFoundException("No user with this email found."));

        // when & then
        assertThatThrownBy(() -> service.delete("luis@gmail.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No user with this email found.");

        // verify
        then(repository).should(times(1)).findByEmail(any(String.class));
        then(repository).shouldHaveNoMoreInteractions();
    }
}