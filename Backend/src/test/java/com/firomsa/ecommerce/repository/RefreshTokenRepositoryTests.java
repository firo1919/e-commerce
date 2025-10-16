package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.RefreshToken;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

@DataJpaTest
@ActiveProfiles("test")
public class RefreshTokenRepositoryTests {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    private Role role;
    private User testUser;
    private RefreshToken testRefreshToken1;
    private RefreshToken testRefreshToken2;

    @BeforeEach
    void setup() {
        role = roleRepository.save(Role.builder().name("USER").build());

        testUser = User.builder()
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role) // role is now initialized
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testRefreshToken1 = RefreshToken.builder()
                .user(testUser)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();

        testRefreshToken2 = RefreshToken.builder()
                .user(testUser)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void RefreshTokenRepository_Save_ReturnSavedRefreshToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        RefreshToken refreshToken = testRefreshToken1;
        refreshToken.setUser(savedUser);

        // Act
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // Assert
        assertThat(savedRefreshToken).isNotNull();
        assertThat(savedRefreshToken.getId()).isNotNull();
        assertThat(savedRefreshToken).usingRecursiveComparison().isEqualTo(refreshToken);
        assertThat(savedRefreshToken.getCreatedAt()).isNotNull();
    }

    @Test
    public void RefreshTokenRepository_FindAll_ReturnMoreThanOneRefreshToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        RefreshToken refreshToken1 = testRefreshToken1;
        RefreshToken refreshToken2 = testRefreshToken2;
        refreshToken1.setUser(savedUser);
        refreshToken2.setUser(savedUser);

        // Act
        refreshTokenRepository.save(refreshToken1);
        refreshTokenRepository.save(refreshToken2);
        List<RefreshToken> savedRefreshTokens = refreshTokenRepository.findAll();

        // Assert
        assertThat(savedRefreshTokens).isNotNull();
        assertThat(savedRefreshTokens.size()).isEqualTo(2);
    }

    @Test
    public void RefreshTokenRepository_FindById_ReturnRefreshToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        RefreshToken refreshToken = testRefreshToken1;
        refreshToken.setUser(savedUser);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // Act
        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findById(savedRefreshToken.getId());

        // Assert
        assertThat(foundRefreshToken).isPresent();
        RefreshToken retrievedRefreshToken = foundRefreshToken.get();
        assertThat(retrievedRefreshToken).isNotNull();
        assertThat(retrievedRefreshToken).usingRecursiveComparison().isEqualTo(savedRefreshToken);
    }

    @Test
    public void RefreshTokenRepository_FindById_ReturnEmpty() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findById(nonExistentId);

        // Assert
        assertThat(foundRefreshToken).isEmpty();
    }

    @Test
    public void RefreshTokenRepository_FindByIdAndExpiresAtAfter_ReturnRefreshToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        RefreshToken refreshToken = testRefreshToken1;
        refreshToken.setUser(savedUser);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // Act
        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository
                .findByIdAndExpiresAtAfter(savedRefreshToken.getId(), LocalDateTime.now());

        // Assert
        assertThat(foundRefreshToken).isPresent();
        RefreshToken retrievedRefreshToken = foundRefreshToken.get();
        assertThat(retrievedRefreshToken).isNotNull();
        assertThat(retrievedRefreshToken).usingRecursiveComparison().isEqualTo(savedRefreshToken);
    }

    @Test
    public void RefreshTokenRepository_FindByIdAndExpiresAtAfter_ReturnEmpty() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        RefreshToken refreshToken = testRefreshToken1;
        refreshToken.setUser(savedUser);
        refreshToken.setExpiresAt(LocalDateTime.now().minusDays(1)); // Expired token
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // Act
        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository
                .findByIdAndExpiresAtAfter(savedRefreshToken.getId(), LocalDateTime.now());

        // Assert
        assertThat(foundRefreshToken).isEmpty();
    }

    @Test
    public void RefreshTokenRepository_DeleteById_DeleteRefreshToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        RefreshToken refreshToken = testRefreshToken1;
        refreshToken.setUser(savedUser);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // Act
        refreshTokenRepository.deleteById(savedRefreshToken.getId());

        // Assert
        assertThat(refreshTokenRepository.existsById(savedRefreshToken.getId())).isFalse();
    }

    @Test
    public void RefreshTokenRepository_Delete_DeleteRefreshToken() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        RefreshToken refreshToken = testRefreshToken1;
        refreshToken.setUser(savedUser);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // Act
        refreshTokenRepository.delete(savedRefreshToken);

        // Assert
        assertThat(refreshTokenRepository.existsById(savedRefreshToken.getId())).isFalse();
    }
}
