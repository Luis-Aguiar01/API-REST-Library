package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.*;
import com.luis.aguiar.exceptions.ErrorModel;
import com.luis.aguiar.mappers.UserMapper;
import com.luis.aguiar.models.User;
import com.luis.aguiar.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Tag(name = "Users", description = "Fornece as operações relacionadas aos usuários da API.")
@RestController
@RequestMapping("library/v1/users")
public class UserController {

    @Autowired
    private UserService service;

    @Operation(summary = "Cria um novo usuário.", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário criado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado, sem permissão ou dados inválidos fornecidos.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já cadastrado no sistema.",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserCreateDto userDto) {
        User user = service.save(userDto);
        UserResponseDto userResponseDto = UserMapper.toResponseDto(user);

        addFindByIdReference(userResponseDto, user.getId());
        addUserUpdateReference(userResponseDto);
        addUserDeleteReference(userResponseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }


    @Operation(summary = "Encontra um usuário pelo ID e exibe os seus dados.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Recurso encontrado e retornado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = BookResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Usuário não encontrado.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Formato inválido para o ID",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable(name = "id") UUID uuid) {
        User user = service.findById(uuid);
        UserResponseDto userResponseDto = UserMapper.toResponseDto(user);

        addUserUpdateReference(userResponseDto);
        addUserDeleteReference(userResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }


    @Operation(summary = "Recebe uma lista de todos os usuários cadastrados.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Recurso retornado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = UserResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Formato inválido para os dados da requisição.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem permissão ou dados inválidos fornecidos.",
                        content = @Content
                ),
    })
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


    @Operation(summary = "Atualiza os dados de um usuário.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Recurso atualizado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = UserResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Formato inválido para os dados da requisição.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem permissão ou dados inválidos fornecidos.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Usuário não encontrado.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
    })
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


    @Operation(summary = "Deleta um usuário.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Usuário deletado com sucesso.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = UserResponseDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro interno do servidor.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Formato inválido para os dados da requisição.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Usuário não autenticado, sem permissão ou dados inválidos fornecidos.",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Usuário não encontrado.",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorModel.class))
                ),
    })
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