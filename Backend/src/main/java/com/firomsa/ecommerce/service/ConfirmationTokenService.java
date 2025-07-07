package com.firomsa.ecommerce.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.exception.InvalidConfirmationTokenException;
import com.firomsa.ecommerce.model.ConfirmationToken;
import com.firomsa.ecommerce.repository.ConfirmationTokenRepository;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private static final long TOKEN_DURATION = 10;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public void add(ConfirmationToken token) {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusMinutes(TOKEN_DURATION);
        token.setCreatedAt(createdAt);
        token.setExpiresAt(expiresAt);
        confirmationTokenRepository.save(token);
    }

    public void verifyToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidConfirmationTokenException("Invalid confirmation token"));
        LocalDateTime now = LocalDateTime.now();
        if(!now.isBefore(confirmationToken.getExpiresAt()) || confirmationToken.getConfirmedAt() != null){
            throw new InvalidConfirmationTokenException("The confirmation token has expired");
        }

        confirmationToken.setConfirmedAt(now);
        confirmationTokenRepository.save(confirmationToken);
    }
}
