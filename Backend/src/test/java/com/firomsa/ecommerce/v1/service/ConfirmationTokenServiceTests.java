package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.exception.InvalidConfirmationTokenException;
import com.firomsa.ecommerce.model.ConfirmationToken;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.ConfirmationTokenRepository;
import com.firomsa.ecommerce.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ConfirmationTokenServiceTests {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;

    private User user;
    private ConfirmationToken token;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(UUID.randomUUID())
                .username("firo")
                .email("e@example.com")
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        token = ConfirmationToken.builder()
                .token("123456")
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(6))
                .build();
    }

    @Test
    public void ConfirmationTokenService_GenerateToken_SavesAndSendsEmail() {
        // Arrange
        given(userRepository.findByUsername("firo")).willReturn(Optional.of(user));

        // Act
        confirmationTokenService.generateToken("firo", "e@example.com");

        // Assert
        verify(confirmationTokenRepository, times(1)).save(any(ConfirmationToken.class));
        verify(emailService, times(1)).sendSimpleMessage(eq("e@example.com"), eq("Account Verification"),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    public void ConfirmationTokenService_GenerateToken_Throws_WhenUserMissing() {
        // Arrange
        given(userRepository.findByUsername("missing")).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> confirmationTokenService.generateToken("missing", "e@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("USER: missing Not found");
    }

    @Test
    public void ConfirmationTokenService_VerifyToken_ActivatesUser() {
        // Arrange
        given(confirmationTokenRepository.findByToken("123456")).willReturn(Optional.of(token));

        // Act
        confirmationTokenService.verifyToken("123456");

        // Assert
        assertThat(user.isActive()).isTrue();
        assertThat(token.getConfirmedAt()).isNotNull();
        verify(userRepository, times(1)).save(user);
        verify(confirmationTokenRepository, times(1)).save(token);
    }

    @Test
    public void ConfirmationTokenService_VerifyToken_Throws_WhenInvalidToken() {
        // Arrange
        given(confirmationTokenRepository.findByToken("bad")).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> confirmationTokenService.verifyToken("bad"))
                .isInstanceOf(InvalidConfirmationTokenException.class)
                .hasMessage("Invalid confirmation token");
    }

    @Test
    public void ConfirmationTokenService_VerifyToken_Throws_WhenExpired() {
        // Arrange
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        given(confirmationTokenRepository.findByToken("123456")).willReturn(Optional.of(token));

        // Act & Assert
        assertThatThrownBy(() -> confirmationTokenService.verifyToken("123456"))
                .isInstanceOf(InvalidConfirmationTokenException.class)
                .hasMessage("The confirmation token has expired");
    }

    @Test
    public void ConfirmationTokenService_ResendToken_SendsWhenNoActiveToken() {
        // Arrange
        given(userRepository.findByUsername("firo")).willReturn(Optional.of(user));
        given(confirmationTokenRepository.findByUserAndExpiresAtAfter(any(User.class), any(LocalDateTime.class)))
                .willReturn(Optional.empty());

        // Act
        confirmationTokenService.resendToken("firo", "e@example.com");

        // Assert
        verify(confirmationTokenRepository, times(1)).save(any(ConfirmationToken.class));
        verify(emailService, times(1)).sendSimpleMessage(eq("e@example.com"), eq("Account Verification"),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    public void ConfirmationTokenService_ResendToken_Throws_WhenUserActive() {
        // Arrange
        user.setActive(true);
        given(userRepository.findByUsername("firo")).willReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> confirmationTokenService.resendToken("firo", "e@example.com"))
                .isInstanceOf(InvalidConfirmationTokenException.class)
                .hasMessage("User already veryfied");
    }

    @Test
    public void ConfirmationTokenService_ResendToken_Throws_WhenActiveTokenExists() {
        // Arrange
        given(userRepository.findByUsername("firo")).willReturn(Optional.of(user));
        given(confirmationTokenRepository.findByUserAndExpiresAtAfter(any(User.class), any(LocalDateTime.class)))
                .willReturn(Optional.of(token));

        // Act & Assert
        assertThatThrownBy(() -> confirmationTokenService.resendToken("firo", "e@example.com"))
                .isInstanceOf(InvalidConfirmationTokenException.class)
                .hasMessage("An active confirmation token already exists");
    }
}
