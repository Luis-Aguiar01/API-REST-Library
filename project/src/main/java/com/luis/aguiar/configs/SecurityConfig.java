package com.luis.aguiar.configs;

import com.luis.aguiar.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,"/library/v1/users").permitAll()
                        .requestMatchers(HttpMethod.GET,"/library/v1/books").permitAll()
                        .requestMatchers(HttpMethod.GET,"/library/v1/books/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/library/v1/books/name").permitAll()
                        .requestMatchers(HttpMethod.GET,"/library/v1/books/author").permitAll()
                        .requestMatchers(HttpMethod.GET,"/library/v1/books/status").permitAll()
                        .requestMatchers(HttpMethod.GET,"/library/v1/authors").permitAll()
                        .requestMatchers(HttpMethod.POST,"/library/v1/users/authenticate").permitAll()
                        .requestMatchers(HttpMethod.POST,"/library/v1/loans").permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}