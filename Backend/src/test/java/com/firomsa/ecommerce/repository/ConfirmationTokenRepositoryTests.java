package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.ConfirmationToken;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

@DataJpaTest
@ActiveProfiles("test")
public class ConfirmationTokenRepositoryTests {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role role;
    private User testUser;
    private ConfirmationToken testToken1;
    private ConfirmationToken testToken2;

    @BeforeEach
    void setup() {
        role = roleRepository.save(Role.builder().name("USER").build());
        testUser = User.builder()
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testToken1 = ConfirmationToken.builder()
                .token("confirmation-token-123")
                .user(testUser)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .createdAt(LocalDateTime.now())
                .build();

        testToken2 = ConfirmationToken.builder()
                .token("confirmation-token-456")
                .user(testUser)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .confirmedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void ConfirmationTokenRepository_Save_ReturnSavedToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        ConfirmationToken token = testToken1;
        token.setUser(savedUser);

        // Act
        ConfirmationToken savedToken = confirmationTokenRepository.save(token);

        // Assert
        assertThat(savedToken).isNotNull();
        assertThat(savedToken.getId()).isNotNull();
        assertThat(savedToken).usingRecursiveComparison().isEqualTo(token);
        assertThat(savedToken.getCreatedAt()).isNotNull();
    }

    @Test
    public void ConfirmationTokenRepository_FindById_ReturnToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        ConfirmationToken token = testToken1;
        token.setUser(savedUser);
        ConfirmationToken savedToken = confirmationTokenRepository.save(token);

        // Act
        Optional<ConfirmationToken> foundToken = confirmationTokenRepository.findById(savedToken.getId());

        // Assert
        assertThat(foundToken).isPresent();
        ConfirmationToken retrievedToken = foundToken.get();
        assertThat(retrievedToken).isNotNull();
        assertThat(retrievedToken).usingRecursiveComparison().isEqualTo(savedToken);
    }

    @Test
    public void ConfirmationTokenRepository_FindById_ReturnEmpty() {
        // Arrange
        Long nonExistentId = 999L;

        // Act
        Optional<ConfirmationToken> foundToken = confirmationTokenRepository.findById(nonExistentId);

        // Assert
        assertThat(foundToken).isEmpty();
    }

    @Test
    public void ConfirmationTokenRepository_FindByToken_ReturnToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        ConfirmationToken token = testToken1;
        token.setUser(savedUser);
        confirmationTokenRepository.save(token);

        // Act
        Optional<ConfirmationToken> foundToken = confirmationTokenRepository.findByToken("confirmation-token-123");

        // Assert
        assertThat(foundToken).isPresent();
        ConfirmationToken retrievedToken = foundToken.get();
        assertThat(retrievedToken).isNotNull();
        assertThat(retrievedToken).usingRecursiveComparison().isEqualTo(token);
        assertThat(retrievedToken.getToken()).isEqualTo("confirmation-token-123");
    }

    @Test
    public void ConfirmationTokenRepository_FindByToken_ReturnEmpty() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        ConfirmationToken token = testToken1;
        token.setUser(savedUser);
        confirmationTokenRepository.save(token);

        // Act
        Optional<ConfirmationToken> foundToken = confirmationTokenRepository.findByToken("nonexistent-token");

        // Assert
        assertThat(foundToken).isEmpty();
    }

    @Test
    public void ConfirmationTokenRepository_FindByUserAndExpiresAtAfter_ReturnToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        ConfirmationToken token = testToken1;
        token.setUser(savedUser);
        confirmationTokenRepository.save(token);

        // Act
        Optional<ConfirmationToken> foundToken = confirmationTokenRepository.findByUserAndExpiresAtAfter(savedUser,
                LocalDateTime.now());

        // Assert
        assertThat(foundToken).isPresent();
        ConfirmationToken retrievedToken = foundToken.get();
        assertThat(retrievedToken).isNotNull();
        assertThat(retrievedToken).usingRecursiveComparison().isEqualTo(token);
    }

    @Test
    public void ConfirmationTokenRepository_FindByUserAndExpiresAtAfter_ReturnEmpty() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        ConfirmationToken token = testToken1;
        token.setUser(savedUser);
        token.setExpiresAt(LocalDateTime.now().minusHours(1)); // Expired token
        confirmationTokenRepository.save(token);

        // Act
        Optional<ConfirmationToken> foundToken = confirmationTokenRepository.findByUserAndExpiresAtAfter(savedUser,
                LocalDateTime.now());

        // Assert
        assertThat(foundToken).isEmpty();
    }

    @Test
    public void ConfirmationTokenRepository_DeleteAllByUserAndConfirmedAtIsNull_DeleteUnconfirmedTokens() {
        // Arrange
        User savedUser = userRepository.save(testUser);

        ConfirmationToken unconfirmedToken = testToken1;
        unconfirmedToken.setUser(savedUser);

        ConfirmationToken confirmedToken = testToken2;
        confirmedToken.setUser(savedUser);

        confirmationTokenRepository.save(unconfirmedToken);
        confirmationTokenRepository.save(confirmedToken);

        // Act
        confirmationTokenRepository.deleteAllByUserAndConfirmedAtIsNull(savedUser);

        // Assert
        assertThat(confirmationTokenRepository.existsById(unconfirmedToken.getId())).isFalse();
        assertThat(confirmationTokenRepository.existsById(confirmedToken.getId())).isTrue();
    }

    @Test
    public void ConfirmationTokenRepository_DeleteById_DeleteToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        ConfirmationToken token = testToken1;
        token.setUser(savedUser);
        ConfirmationToken savedToken = confirmationTokenRepository.save(token);

        // Act
        confirmationTokenRepository.deleteById(savedToken.getId());

        // Assert
        assertThat(confirmationTokenRepository.existsById(savedToken.getId())).isFalse();
    }

    @Test
    public void ConfirmationTokenRepository_Delete_DeleteToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        ConfirmationToken token = testToken1;
        token.setUser(savedUser);
        ConfirmationToken savedToken = confirmationTokenRepository.save(token);

        // Act
        confirmationTokenRepository.delete(savedToken);

        // Assert
        assertThat(confirmationTokenRepository.existsById(savedToken.getId())).isFalse();
    }
}
