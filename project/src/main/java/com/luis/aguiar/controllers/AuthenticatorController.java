package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.*;
import com.luis.aguiar.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Fornece autenticação para acessar os recursos da API.")
@RestController
@RequestMapping("library/v1/users")
@RequiredArgsConstructor
public class AuthenticatorController {

    private final AuthenticationService service;

    @Operation(summary = "Fornece uma operação de autenticação.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operação concluída com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "E-mail fornecido inválido.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Senha inválida para acessar a conta."
            )
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@RequestBody @Valid AuthenticateRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}