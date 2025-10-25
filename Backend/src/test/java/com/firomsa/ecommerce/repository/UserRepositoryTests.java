package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private final User testUser1 = User.builder()
            .username("firo")
            .email("example@gmail.com")
            .firstName("Firomsa")
            .lastName("Assefa")
            .password("123")
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    private final User testUser2 = User.builder()
            .username("kira")
            .email("example2@gmail.com")
            .firstName("Kirubel")
            .lastName("Assefa")
            .password("123")
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    @Test
    public void UserRepository_Save_ReturnSavedUser() {
        // Arrange
        User user = testUser1;

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(testUser1);
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    public void UserRepository_FindAll_ReturnMoreThanOneUser() {
        // Arrange
        User user1 = testUser1;
        User user2 = testUser2;

        // Act
        userRepository.save(user1);
        userRepository.save(user2);
        List<User> savedUsers = userRepository.findAll();

        // Assert
        assertThat(savedUsers).isNotNull();
        assertThat(savedUsers.size()).isEqualTo(2);
        assertThat(savedUsers).extracting(User::getUsername).containsExactlyInAnyOrder("firo", "kira");
        assertThat(savedUsers).extracting(User::getEmail).containsExactlyInAnyOrder("example@gmail.com",
                "example2@gmail.com");
    }

    @Test
    public void UserRepository_ExistsByEmail_ReturnTrue() {
        // Arrange
        User user = testUser1;

        // Act
        userRepository.save(user);
        boolean userExistsByEmail = userRepository.existsByEmail("example@gmail.com");

        // Assert
        assertThat(userExistsByEmail).isTrue();
    }

    @Test
    public void UserRepository_ExistsByEmail_ReturnFalse() {
        // Arrange
        User user = testUser1;

        // Act
        userRepository.save(user);
        boolean userExistsByEmail = userRepository.existsByEmail("nonexistent@gmail.com");

        // Assert
        assertThat(userExistsByEmail).isFalse();
    }

    @Test
    public void UserRepository_ExistsByEmailAndIdNot_ReturnTrue() {
        // Arrange
        User user1 = testUser1;
        User user2 = testUser2;

        // Act
        userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        boolean otherUserByThisEmailExists = userRepository.existsByEmailAndIdNot("example@gmail.com",
                savedUser2.getId());

        // Assert
        assertThat(otherUserByThisEmailExists).isTrue();
    }

    @Test
    public void UserRepository_ExistsByEmailAndIdNot_ReturnFalse() {
        // Arrange
        User user1 = testUser1;
        User user2 = testUser2;

        // Act
        userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        boolean otherUserByThisEmailExists = userRepository.existsByEmailAndIdNot("example2@gmail.com",
                savedUser2.getId());

        // Assert
        assertThat(otherUserByThisEmailExists).isFalse();
    }

    @Test
    public void UserRepository_ExistsByUsername_ReturnTrue() {
        // Arrange
        User user = testUser1;

        // Act
        userRepository.save(user);
        boolean userExistsUserName = userRepository.existsByUsername("firo");

        // Assert
        assertThat(userExistsUserName).isTrue();
    }

    @Test
    public void UserRepository_ExistsByUsername_ReturnFalse() {
        // Arrange
        User user = testUser1;

        // Act
        userRepository.save(user);
        boolean userExistsUserName = userRepository.existsByUsername("nonexistent");

        // Assert
        assertThat(userExistsUserName).isFalse();
    }

    @Test
    public void UserRepository_ExistsByUsernameAndIdNot_ReturnTrue() {
        // Arrange
        User user1 = testUser1;
        User user2 = testUser2;

        // Act
        userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        boolean otherUserByThisUsernameExists = userRepository.existsByUsernameAndIdNot("firo", savedUser2.getId());

        // Assert
        assertThat(otherUserByThisUsernameExists).isTrue();
    }

    @Test
    public void UserRepository_ExistsByUsernameAndIdNot_ReturnFalse() {
        // Arrange
        User user1 = testUser1;
        User user2 = testUser2;

        // Act
        userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        boolean otherUserByThisUsernameExists = userRepository.existsByUsernameAndIdNot("kira", savedUser2.getId());

        // Assert
        assertThat(otherUserByThisUsernameExists).isFalse();
    }

    @Test
    public void UserRepository_FindById_ReturnUser() {
        // Arrange
        User user = testUser1;

        // Act
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(foundUser).isPresent();
        User retrievedUser = foundUser.get();
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser).usingRecursiveComparison().isEqualTo(savedUser);
    }

    @Test
    public void UserRepository_FindById_ReturnEmpty() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<User> foundUser = userRepository.findById(nonExistentId);

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    public void UserRepository_FindByUsername_ReturnUser() {
        // Arrange
        User user = testUser1;

        // Act
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByUsername("firo");

        // Assert
        assertThat(foundUser).isPresent();
        User retrievedUser = foundUser.get();
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser).usingRecursiveComparison().isEqualTo(user);
        assertThat(retrievedUser.isActive()).isTrue();
    }

    @Test
    public void UserRepository_FindByUsername_ReturnEmpty() {
        // Arrange
        User user = testUser1;

        // Act
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    public void UserRepository_FindByEmail_ReturnUser() {
        // Arrange
        User user = testUser1;

        // Act
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByEmail("example@gmail.com");

        // Assert
        assertThat(foundUser).isPresent();
        User retrievedUser = foundUser.get();
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser).usingRecursiveComparison().isEqualTo(user);
        assertThat(retrievedUser.isActive()).isTrue();
    }

    @Test
    public void UserRepository_FindByEmail_ReturnEmpty() {
        // Arrange
        User user = testUser1;

        // Act
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@gmail.com");

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    public void UserRepository_DeleteById_DeleteUser() {
        // Arrange
        User user = testUser1;
        User savedUser = userRepository.save(user);

        // Act
        userRepository.deleteById(savedUser.getId());

        // Assert
        assertThat(userRepository.existsById(savedUser.getId())).isFalse();
    }

    @Test
    public void UserRepository_Delete_DeleteUser() {
        // Arrange
        User user = testUser1;
        User savedUser = userRepository.save(user);

        // Act
        userRepository.delete(savedUser);

        // Assert
        assertThat(userRepository.existsById(savedUser.getId())).isFalse();
    }

}
