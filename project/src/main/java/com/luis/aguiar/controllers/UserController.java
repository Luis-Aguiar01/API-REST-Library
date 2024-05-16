package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.*;
import com.luis.aguiar.mappers.UserMapper;
import com.luis.aguiar.models.User;
import com.luis.aguiar.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("library/v1/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserCreateDto userDto) {
        User user = service.save(userDto);
        UserResponseDto userResponseDto = UserMapper.toResponseDto(user);

        addFindByIdReference(userResponseDto, user.getId());
        addUserUpdateReference(userResponseDto);
        addUserDeleteReference(userResponseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable(name = "id") UUID uuid) {
        User user = service.findById(uuid);
        UserResponseDto userResponseDto = UserMapper.toResponseDto(user);

        addUserUpdateReference(userResponseDto);
        addUserDeleteReference(userResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> findAllUsers(@RequestParam int page, @RequestParam int quantity) {
        List<User> users = service.findAll(page, quantity);
        List<UserResponseDto> usersDto = users.stream()
                .map(user -> {
                    UUID uuid = user.getId();
                    UserResponseDto userResponseDto = UserMapper.toResponseDto(user);
                    addFindByIdReference(userResponseDto, uuid);

                    return userResponseDto;
                })
                .toList();
        return ResponseEntity.ok(usersDto);
    }

    @PutMapping("/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') AND #email == authentication.principal.username")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable(name = "email") String email,
                                                      @RequestBody UserCreateDto userDto) {
        User user = service.update(email, UserMapper.toUser(userDto));
        UserResponseDto userResponseDto = UserMapper.toResponseDto(user);

        addUserDeleteReference(userResponseDto);
        addFindByIdReference(userResponseDto, user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') AND #email == authentication.principal.username")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "email") String email) {
        service.delete(email);
        return ResponseEntity.ok("User deleted successfully.");
    }

    private void addUserUpdateReference(UserResponseDto userResponseDto) {
        userResponseDto.add(linkTo(methodOn(UserController.class)
                .updateUser(userResponseDto.getEmail(), null))
                .withRel("update-user")
                .withType(HttpMethod.PUT.name()));
    }

    private void addUserDeleteReference(UserResponseDto userResponseDto) {
        userResponseDto.add(linkTo(methodOn(UserController.class)
                .deleteUser(userResponseDto.getEmail()))
                .withRel("delete-user")
                .withType(HttpMethod.DELETE.name()));
    }

    private void addFindByIdReference(UserResponseDto userResponseDto, UUID uuid) {
        userResponseDto.add(linkTo(methodOn(UserController.class)
                .findUserById(uuid))
                .withSelfRel()
                .withType(HttpMethod.GET.name()));
    }
}