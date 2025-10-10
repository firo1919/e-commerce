package com.firomsa.ecommerce.service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.config.AuthConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

// service/JWTAuthService.java
@Service
public class JWTAuthService {

    // JWT token duration in minutes
    private static final int JWT_DURATION_MINUTES = 15;

    private final AuthConfig authConfig;

    public JWTAuthService(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    // code to generate Token

    public String generateToken(String subject) {
        String tokenId = UUID.randomUUID().toString();
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiresAt = new Date(issuedAt.getTime() + TimeUnit.MINUTES.toMillis(JWT_DURATION_MINUTES));
        return Jwts.builder()
                .header()
                .keyId(tokenId)
                .and()
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(authConfig.getSecretKey())
                .compact();
    }

    // code to get Claims

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(authConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // code to check if token is valid

    public boolean isValidToken(String token) {
        return getExpirationDate(token).after(
                new Date(System.currentTimeMillis()));
    }

    // code to check if token is valid as per username

    public boolean isValidToken(String token, String username) {
        String tokenUserName = getSubject(token);

        return (username.equals(tokenUserName) && !isTokenExpired(token));
    }

    // code to check if token is expired

    public boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(
                new Date(System.currentTimeMillis()));
    }

    // code to get expiration date

    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    // code to get subject

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }
}
