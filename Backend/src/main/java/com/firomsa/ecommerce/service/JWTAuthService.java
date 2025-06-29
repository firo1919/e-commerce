package com.firomsa.ecommerce.service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JWTAuthService {
    @Value("${private.key}")
    private RSAPrivateKey privateKey;

    @Value("${public.key}")
    private RSAPublicKey publicKey;

    private final Random random = new Random();

    // code to generate Token
    public String generateToken(String subject) {
        String tokenId = String.valueOf(random.nextInt(10000));
        return Jwts.builder()
                .header()
                .keyId(tokenId)
                .and()
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    // code to get Claims
    public Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // code to check if token is valid
    public boolean isValidToken(String token) {
        return getExpirationDate(token).after(new Date(System.currentTimeMillis()));
    }

    // code to check if token is valid as per username
    public boolean isValidToken(String token, String username) {
        String tokenUserName = getSubject(token);
        return (username.equals(tokenUserName) && !isTokenExpired(token));
    }

    // code to check if token is expired
    public boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date(System.currentTimeMillis()));
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
