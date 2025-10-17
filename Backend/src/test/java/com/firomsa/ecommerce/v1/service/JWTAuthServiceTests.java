package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.firomsa.ecommerce.config.AuthConfig;

import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
public class JWTAuthServiceTests {

    @Mock
    private AuthConfig authConfig;

    @InjectMocks
    private JWTAuthService jwtAuthService;

    @Test
    public void JWTAuthService_GenerateAndValidateToken_Works() {
        // Arrange
        var secretKey = Keys.hmacShaKeyFor(new byte[32]);
        org.mockito.BDDMockito.given(authConfig.getSecretKey()).willReturn(secretKey);

        // Act
        String token = jwtAuthService.generateToken("user1");

        // Assert
        assertThat(token).isNotBlank();
        assertThat(jwtAuthService.isValidToken(token)).isTrue();
        assertThat(jwtAuthService.getSubject(token)).isEqualTo("user1");
        Date exp = jwtAuthService.getExpirationDate(token);
        assertThat(exp.after(new Date(System.currentTimeMillis()))).isTrue();
    }

    @Test
    public void JWTAuthService_UsernameValidation_Works() {
        // Arrange
        var secretKey = Keys.hmacShaKeyFor(new byte[32]);
        org.mockito.BDDMockito.given(authConfig.getSecretKey()).willReturn(secretKey);

        // Act
        String token = jwtAuthService.generateToken("user1");

        // Assert
        assertThat(jwtAuthService.isValidToken(token, "user1")).isTrue();
        assertThat(jwtAuthService.isValidToken(token, "user2")).isFalse();
    }
}
