package com.luis.aguiar.services;

import com.luis.aguiar.dto.AuthenticateRequest;
import com.luis.aguiar.dto.AuthenticateResponse;
import com.luis.aguiar.exceptions.UniqueDataViolationException;
import com.luis.aguiar.models.User;
import com.luis.aguiar.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
