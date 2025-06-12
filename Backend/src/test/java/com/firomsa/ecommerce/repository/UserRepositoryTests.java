package com.firomsa.ecommerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRepository_Save_ReturnSavedUser(){

        //Arrange
        User user = new User();
        user.setUserName("firo");
        user.setEmail("example@gmail.com");
        user.setFirstName("Firomsa");
        user.setLastName("Assefa");
        user.setPassword("123");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        //Act
        User savedUser = userRepository.save(user);

        //Assert
        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void UserRepository_FindAll_ReturnMoreThanOneUser(){

        User user1 = new User();
        user1.setUserName("firo");
        user1.setEmail("example@gmail.com");
        user1.setFirstName("Firomsa");
        user1.setLastName("Assefa");
        user1.setPassword("123");
        user1.setRole(Role.CUSTOMER);
        user1.setActive(true);
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());

        User user2 = new User();
        user2.setUserName("kira");
        user2.setEmail("example2@gmail.com");
        user2.setFirstName("Kirubel");
        user2.setLastName("Assefa");
        user2.setPassword("123");
        user2.setRole(Role.CUSTOMER);
        user2.setActive(true);
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> savedUsers = userRepository.findAll();

        Assertions.assertThat(savedUsers).isNotNull();
        Assertions.assertThat(savedUsers.size()).isEqualTo(2);
    }

    @Test
    public void UserRepository_ExistsByEmail_ReturnTrue(){

        User user = new User();
        user.setUserName("firo");
        user.setEmail("example@gmail.com");
        user.setFirstName("Firomsa");
        user.setLastName("Assefa");
        user.setPassword("123");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        boolean userExistsByEmail = userRepository.existsByEmail("example@gmail.com");

        Assertions.assertThat(userExistsByEmail).isTrue();
    }

    @Test
    public void UserRepository_ExistsByEmailIdNot_ReturnTrue(){

        User user1 = new User();
        user1.setUserName("firo");
        user1.setEmail("example@gmail.com");
        user1.setFirstName("Firomsa");
        user1.setLastName("Assefa");
        user1.setPassword("123");
        user1.setRole(Role.CUSTOMER);
        user1.setActive(true);
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());

        User user2 = new User();
        user2.setUserName("kira");
        user2.setEmail("example@gmail.com");
        user2.setFirstName("Kirubel");
        user2.setLastName("Assefa");
        user2.setPassword("123");
        user2.setRole(Role.CUSTOMER);
        user2.setActive(true);
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user1);
        User user = userRepository.save(user2);

        boolean otherUserByThisEmailExists = userRepository.existsByEmailAndIdNot("example@gmail.com", user.getId());

        Assertions.assertThat(otherUserByThisEmailExists).isTrue();
    }

    @Test
    public void UserRepository_ExistsByUserName_ReturnTrue(){

        User user = new User();
        user.setUserName("firo");
        user.setEmail("example@gmail.com");
        user.setFirstName("Firomsa");
        user.setLastName("Assefa");
        user.setPassword("123");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        boolean userExistsUserName = userRepository.existsByUserName("firo");

        Assertions.assertThat(userExistsUserName).isTrue();
    }

    @Test
    public void UserRepository_FindById_ReturnUser(){

        User user = new User();
        user.setUserName("firo");
        user.setEmail("example@gmail.com");
        user.setFirstName("Firomsa");
        user.setLastName("Assefa");
        user.setPassword("123");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        User retrievedUser = userRepository.findById(savedUser.getId()).get();

        Assertions.assertThat(retrievedUser).isNotNull();
    }
}
