package com.luis.aguiar.services;

import com.luis.aguiar.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserDetailsService service;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = service.loadUserByUsername(request.getEmail());
        var token = jwtService.generateToken(user);

        return AuthenticateResponse.builder()
                .token(token)
                .build();
    }
}