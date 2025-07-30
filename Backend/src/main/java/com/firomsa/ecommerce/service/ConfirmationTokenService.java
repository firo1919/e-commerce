package com.firomsa.ecommerce.service;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.exception.InvalidConfirmationTokenException;
import com.firomsa.ecommerce.model.ConfirmationToken;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.ConfirmationTokenRepository;
import com.firomsa.ecommerce.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;
    private static final long TOKEN_DURATION = 10;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository,
            UserRepository userRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userRepository = userRepository;
    }

    public void add(String emailtoken, String username) {
        ConfirmationToken token = ConfirmationToken.builder().token(emailtoken).build();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("USER: " + username + " Not found"));

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusMinutes(TOKEN_DURATION);
        token.setCreatedAt(createdAt);
        token.setExpiresAt(expiresAt);
        token.setUser(user);
        confirmationTokenRepository.save(token);
    }

    @Transactional
    public void verifyToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidConfirmationTokenException("Invalid confirmation token"));
        if (confirmationToken.getUser().isActive()) {
            throw new InvalidConfirmationTokenException("User already veryfied");
        }
        LocalDateTime now = LocalDateTime.now();
        if (!now.isBefore(confirmationToken.getExpiresAt())) {
            throw new InvalidConfirmationTokenException("The confirmation token has expired");
        } else if (confirmationToken.getConfirmedAt() != null) {
            throw new InvalidConfirmationTokenException("Token already used");
        }

        User user = confirmationToken.getUser();
        user.setActive(true);
        confirmationToken.setConfirmedAt(now);
        userRepository.save(user);
        confirmationTokenRepository.save(confirmationToken);
    }
}
