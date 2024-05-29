package com.luis.aguiar.services;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Method;
import java.security.Key;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import io.jsonwebtoken.Jwts;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService service;
    @Mock
    private UserDetails userDetails;
    private SimpleGrantedAuthority authority;
    private List<GrantedAuthority> authorities;
    private Key signKey;

    @BeforeEach
    void setUp() throws Exception {
        authority = new SimpleGrantedAuthority("ROLE_USER");
        authorities = Collections.singletonList(authority);
        signKey = getPrivateSignKey();
    }

    @Test
    @DisplayName(
            "Should return a token with extra claims " +
            "when a valid map of claims is provided to method generateToken."
    )
    void shouldReturnATokenWithExtraClaims_whenAValidMapOfClaimsIsProvidedToMethodGenerateToken() {
        // given
        Map<String, Object> extraClaims = new HashMap<>(Map.of("key", "value"));
        given(userDetails.getUsername()).willReturn("user");
        given(userDetails.getAuthorities()).willAnswer(invocation -> {
            return authorities;
        });

        // when
        String token = service.generateToken(extraClaims, userDetails);

        // then
        assertThat(token).isNotNull();
        String extractedUsername = service.extractUsername(token);
        assertThat(extractedUsername).isEqualTo("user");
        String extractedKey = service.extractClaims(token, claims -> claims.get("key", String.class));
        assertThat(extractedKey).isEqualTo("value");
        String extractedRole = service.extractClaims(token, claims -> claims.get("role", String.class));
        assertThat(extractedRole).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should return a token without extra claims.")
    void shouldReturnATokenWithoutExtraClaims() {
        // given
        given(userDetails.getUsername()).willReturn("user");
        given(userDetails.getAuthorities()).willAnswer(invocation -> {
            return authorities;
        });

        // when
        String token = service.generateToken(userDetails);

        // then
        assertThat(token).isNotNull();
        String extractedUsername = service.extractUsername(token);
        assertThat(extractedUsername).isEqualTo("user");
        String extractedRole = service.extractClaims(token, claims -> claims.get("role", String.class));
        assertThat(extractedRole).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should validate token correctly.")
    void shouldValidateTokenCorrectly() {
        // given
        given(userDetails.getUsername()).willReturn("user");
        given(userDetails.getAuthorities()).willAnswer(invocation -> {
            return authorities;
        });
        String token = service.generateToken(userDetails);
        String invalidToken = Jwts.builder()
                .setSubject("admin")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 24 * 60))
                .signWith(signKey)
                .compact();

        // when
        boolean isValid = service.isValid(token, userDetails);
        boolean isUserInvalid = service.isValid(invalidToken, userDetails);

        // then
        assertThat(isValid).isTrue();
        assertThat(isUserInvalid).isFalse();
    }

    @Test
    @DisplayName("Should check if the token is expired")
    void shouldCheckIfTokenIsExpired() {
        // given
        String expiredToken = Jwts.builder()
                .setSubject("user")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .signWith(signKey)
                .compact();

        // when
        boolean isExpired = false;
        try {
            isExpired = service.isExpired(expiredToken);
        } catch (ExpiredJwtException e) {
            isExpired = true;
        }

        // then
        assertThat(isExpired).isTrue();
    }

    private Key getPrivateSignKey() throws Exception {
        Method method = JwtService.class.getDeclaredMethod("getSignKey");
        method.setAccessible(true);
        return (Key) method.invoke(service);
    }
}