package com.luis.aguiar.repositories;

import com.luis.aguiar.enums.Role;
import com.luis.aguiar.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDate;
import java.util.HashSet;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setup() {
        user = new User(
                null,
                "Luis",
                "Aguiar",
                "luis@gmail.com",
                "123456",
                LocalDate.of(2000, 10, 10),
                true, Role.USER,
                new HashSet<>()
        );
        userRepository.save(user);
    }

    @Test
    @DisplayName("Should return a User Optional when an user email is passed as an argument to findByEmail method.")
    void shouldReturnAUserOptional_whenAnUserEmailIsPassedAsAnArgumentToFindByEmailMethod() {
        var userOptional = userRepository.findByEmail("luis@gmail.com");

        assertThat(userOptional.isPresent()).isTrue();
        assertThat(userOptional.get().getEmail()).isEqualTo("luis@gmail.com");
        assertThat(userOptional.get().getFirstName()).isEqualTo("Luis");
        assertThat(userOptional.get().getLastName()).isEqualTo("Aguiar");

        userOptional = userRepository.findByEmail("ana@gmail.com");

        assertThat(userOptional.isPresent()).isFalse();
    }
}