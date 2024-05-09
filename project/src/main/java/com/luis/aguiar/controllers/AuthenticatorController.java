package com.luis.aguiar.controllers;

import com.luis.aguiar.dto.AuthenticateRequest;
import com.luis.aguiar.dto.AuthenticateResponse;
import com.luis.aguiar.dto.RegisterRequest;
import com.luis.aguiar.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("library/v1/users")
@RequiredArgsConstructor
public class AuthenticatorController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@RequestBody @Valid AuthenticateRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}