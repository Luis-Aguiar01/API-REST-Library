package com.luis.aguiar.services;

import com.luis.aguiar.dto.AuthenticateRequest;
import com.luis.aguiar.dto.AuthenticateResponse;
import com.luis.aguiar.dto.RegisterRequest;
import com.luis.aguiar.dto.UserResponseDto;
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

    private final UserRepository repository;
    private final UserDetailsService service;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {
        try {
            User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .birthDate(request.getBirthDate())
                .role(User.Role.NORMAL)
                .hasBookOnLoan(Boolean.TRUE)
                .build();

            repository.save(user);
        }
        catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new UniqueDataViolationException("A user with this email has already been registered.");
        }
    }

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
