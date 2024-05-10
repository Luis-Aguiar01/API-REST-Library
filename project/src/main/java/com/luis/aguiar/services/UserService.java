package com.luis.aguiar.services;

import com.luis.aguiar.dto.UserCreateDto;
import com.luis.aguiar.exceptions.EntityNotFoundException;
import com.luis.aguiar.exceptions.UniqueDataViolationException;
import com.luis.aguiar.enums.Role;
import com.luis.aguiar.models.User;
import com.luis.aguiar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder encoder;

    public User save(UserCreateDto userDto) {
        try {
            User user = User.builder()
                    .firstName(userDto.getFirstName())
                    .lastName(userDto.getLastName())
                    .email(userDto.getEmail())
                    .password(encoder.encode(userDto.getPassword()))
                    .role(Role.USER)
                    .hasBookOnLoan(Boolean.TRUE)
                    .birthDate(userDto.getBirthDate())
                    .build();
            return repository.save(user);
        }
        catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new UniqueDataViolationException("A user with this email has already been registered.");
        }
    }

    public User findById(UUID uuid) {
        return repository.findById(uuid).orElseThrow(
                () -> new EntityNotFoundException("No user with this ID found.")
        );
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User update(String email, User newDataUser) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("No user with this email found."));

        user.setFirstName(newDataUser.getFirstName());
        user.setLastName(newDataUser.getLastName());
        user.setEmail(newDataUser.getEmail());
        user.setPassword(encoder.encode(newDataUser.getPassword()));
        user.setBirthDate(newDataUser.getBirthDate());

        return repository.save(user);
    }

    public void delete(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("No user with this email found."));
        repository.delete(user);
    }
}
