package com.firomsa.ecommerce.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRepository_Save_ReturnSavedUser(){

        //Arrange
        User user = getDefaultUser("firo", "example@gmail.com", "Firomsa");

        //Act
        User savedUser = userRepository.save(user);

        //Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void UserRepository_FindAll_ReturnMoreThanOneUser(){

        User user1 = getDefaultUser("firo", "example@gmail.com", "Firomsa");
        User user2 = getDefaultUser("kira", "example2@gmail.com", "Kirubel");

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> savedUsers = userRepository.findAll();

        assertThat(savedUsers).isNotNull();
        assertThat(savedUsers.size()).isEqualTo(2);
    }

    @Test
    public void UserRepository_ExistsByEmail_ReturnTrue(){

        User user = getDefaultUser("firo", "example@gmail.com", "Firomsa");

        userRepository.save(user);

        boolean userExistsByEmail = userRepository.existsByEmail("example@gmail.com");

        assertThat(userExistsByEmail).isTrue();
    }

    @Test
    public void UserRepository_ExistsByEmailIdNot_ReturnTrue(){

        User user1 = getDefaultUser("firo", "example@gmail.com", "Firomsa");
        User user2 = getDefaultUser("kira", "example2@gmail.com", "Kirubel");

        userRepository.save(user1);
        User user = userRepository.save(user2);

        boolean otherUserByThisEmailExists = userRepository.existsByEmailAndIdNot("example@gmail.com", user.getId());

        assertThat(otherUserByThisEmailExists).isTrue();
    }

    @Test
    public void UserRepository_ExistsByUserName_ReturnTrue(){

        User user = getDefaultUser("firo", "example@gmail.com", "Firomsa");

        userRepository.save(user);

        boolean userExistsUserName = userRepository.existsByUserName("firo");

        assertThat(userExistsUserName).isTrue();
    }

    @Test
    public void UserRepository_FindById_ReturnUser(){

        User user = getDefaultUser("firo", "example@gmail.com", "Firomsa");

        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        User retrievedUser = foundUser.get();
        assertThat(retrievedUser).isNotNull();
    }

    @Test
    public void UserRepository_DeleteById_ReturnDeletedUser(){
        User user = getDefaultUser("firo", "example@gmail.com", "Firomsa");
        User savedUser = userRepository.save(user);

        userRepository.deleteById(savedUser.getId());

        assertThat(userRepository.existsById(savedUser.getId())).isFalse();
    }
    private static User getDefaultUser(String userName, String email, String firstName) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .userName(userName)
                .email(email)
                .firstName(firstName)
                .lastName("Assefa")
                .password("123")
                .role(Role.CUSTOMER)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
