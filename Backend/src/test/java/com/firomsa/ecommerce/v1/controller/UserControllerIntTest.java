package com.firomsa.ecommerce.v1.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.UserRepository;
import com.firomsa.ecommerce.v1.dto.UserResponseDTO;
import com.firomsa.ecommerce.v1.service.JWTAuthService;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTAuthService jwtAuthService;

    private Role role = Role.builder().name("USER").id(1).build();
    private User firstUser = User.builder()
            .username("firo1")
            .email("example@gmail.com")
            .firstName("Firomsa")
            .lastName("Assefa")
            .password("123")
            .role(role)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .active(true)
            .build();
    private User secondUser = User.builder()
            .username("firo2")
            .email("example2@gmail.com")
            .firstName("Firomsa")
            .lastName("Assefa")
            .password("123")
            .role(role)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .active(true)
            .build();
    private User userAgent = User.builder()
            .username("useragent")
            .email("useragent@gmail.com")
            .firstName("User")
            .lastName("Agent")
            .password("123")
            .role(role)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .active(true)
            .build();

    @Test
    void shouldStartPostgresContainer() {
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void UserController_GetAllUsers_ReturnsListOfUsers() {
        // Arrange
        userRepository.saveAll(List.of(firstUser, secondUser));
        userRepository.findAll()
                .forEach(user -> System.out.println(user.getUsername() + " - " + user.getRole().getName()));

        String token = jwtAuthService.generateToken("firo");

        // Act
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<UserResponseDTO[]> response = restTemplate.exchange("/api/v1/users", HttpMethod.GET,
                requestEntity,
                UserResponseDTO[].class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        List<UserResponseDTO> body = List.of(response.getBody());
        assertThat(body).hasSize(3);
    }
}
