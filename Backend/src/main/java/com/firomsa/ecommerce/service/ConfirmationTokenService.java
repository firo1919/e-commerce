package com.firomsa.ecommerce.service;

import java.time.LocalDateTime;
import java.util.Random;

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
    private final EmailService emailService;

    private static final long TOKEN_DURATION = 6;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository,
            UserRepository userRepository, EmailService emailService) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void generateToken(String username, String email) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("USER: " + username + " Not found"));
        String emailToken = createRandomOneTimePassword();
        ConfirmationToken token = ConfirmationToken.builder().token(emailToken).build();

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusMinutes(TOKEN_DURATION);
        token.setCreatedAt(createdAt);
        token.setExpiresAt(expiresAt);
        token.setUser(user);
        confirmationTokenRepository.save(token);
        emailService.sendSimpleMessage(email, "Account Verification", emailToken);
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

    public void resendToken(String username, String email) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("USER: " + username + " Not found"));
        if (user.isActive()) {
            throw new InvalidConfirmationTokenException("User already veryfied");
        }
        if (confirmationTokenRepository.findByUserAndExpiresAtAfter(user, LocalDateTime.now()).isPresent()) {
            throw new InvalidConfirmationTokenException("An active confirmation token already exists");
        }
        String emailToken = createRandomOneTimePassword();
        ConfirmationToken token = ConfirmationToken.builder().token(emailToken).build();

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusMinutes(TOKEN_DURATION);
        token.setCreatedAt(createdAt);
        token.setExpiresAt(expiresAt);
        token.setUser(user);
        confirmationTokenRepository.save(token);
        emailService.sendSimpleMessage(email, "Account Verification", emailToken);
    }

    public static String createRandomOneTimePassword() {
        Random random = new Random();
        StringBuilder oneTimePassword = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int randomNumber = random.nextInt(10);
            oneTimePassword.append(randomNumber);
        }
        return oneTimePassword.toString().trim();
    }
}
