package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.UserCreateDto;
import com.luis.aguiar.dto.UserResponseDto;
import com.luis.aguiar.mappers.UserMapper;
import com.luis.aguiar.models.User;
import com.luis.aguiar.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("library/v1/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserCreateDto userDto) {
        User user = service.save(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponseDto(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable(name = "id") UUID uuid) {
        User user = service.findById(uuid);
        return ResponseEntity.status(HttpStatus.FOUND).body(UserMapper.toResponseDto(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> findAllUsers() {
        List<User> users = service.findAll();
        List<UserResponseDto> usersDto = users.stream()
                .map(UserMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(usersDto);
    }

    @PutMapping("/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') AND #email == authentication.principal.username")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable(name = "email") String email,
                                                      @RequestBody UserCreateDto userDto) {
        User user = service.update(email, UserMapper.toUser(userDto));
        return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toResponseDto(user));
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') AND #email == authentication.principal.username")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "email") String email) {
        service.delete(email);
        return ResponseEntity.ok("User deleted successfully.");
    }
}