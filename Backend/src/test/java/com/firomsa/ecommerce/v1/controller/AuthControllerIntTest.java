package com.firomsa.ecommerce.v1.controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firomsa.ecommerce.model.ConfirmationToken;
import com.firomsa.ecommerce.model.RefreshToken;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.ConfirmationTokenRepository;
import com.firomsa.ecommerce.repository.RefreshTokenRepository;
import com.firomsa.ecommerce.repository.UserRepository;
import com.firomsa.ecommerce.v1.dto.ConfirmationTokenDTO;
import com.firomsa.ecommerce.v1.dto.LoginUserDTO;
import com.firomsa.ecommerce.v1.dto.RefreshTokenDTO;
import com.firomsa.ecommerce.v1.dto.UserRequestDTO;
import com.firomsa.ecommerce.v1.service.EmailService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ConfirmationTokenRepository confirmationTokenRepository;

        @Autowired
        private RefreshTokenRepository refreshTokenRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private EmailService emailService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        private Role role;
        private User testUser;

        @BeforeEach
        void setUp() {
                role = Role.builder().name("USER").id(1).build();
                testUser = User.builder()
                                .username("testuser")
                                .email("test@example.com")
                                .firstName("Test")
                                .lastName("User")
                                .password("password123")
                                .role(role)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .active(false)
                                .build();
        }

        @Test
        void AuthController_RegisterUser_ReturnsRegisterDTO_WhenValidData() throws Exception {
                // Arrange
                UserRequestDTO userRequest = UserRequestDTO.builder()
                                .username("newuser")
                                .email("newuser@example.com")
                                .firstName("New")
                                .lastName("User")
                                .password("password123")
                                .build();
                doNothing().when(emailService).sendSimpleMessage(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString());

                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequest)))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.message",
                                                CoreMatchers.containsString("Account created successfully")))
                                .andExpect(jsonPath("$.user.username", CoreMatchers.is(userRequest.getUsername())))
                                .andExpect(jsonPath("$.user.email", CoreMatchers.is(userRequest.getEmail())))
                                .andExpect(jsonPath("$.user.firstName", CoreMatchers.is(userRequest.getFirstName())))
                                .andExpect(jsonPath("$.user.lastName", CoreMatchers.is(userRequest.getLastName())));
        }

        @Test
        void AuthController_RegisterUser_Returns400_WhenInvalidData() throws Exception {
                // Arrange
                UserRequestDTO invalidRequest = UserRequestDTO.builder()
                                .username("") // Invalid: empty username
                                .email("invalid-email") // Invalid: malformed email
                                .firstName("Valid")
                                .lastName("User")
                                .password("password123")
                                .build();

                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void AuthController_ResendToken_ReturnsRegisterDTO_WhenUserExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                doNothing().when(emailService).sendSimpleMessage(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString());
                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/resend-token")
                                .param("email", savedUser.getEmail())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.message",
                                                CoreMatchers.containsString("confirmation token is sent")))
                                .andExpect(jsonPath("$.user.email", CoreMatchers.is(savedUser.getEmail())));
        }

        @Test
        void AuthController_ResendToken_Returns404_WhenUserNotFound() throws Exception {
                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/resend-token")
                                .param("email", "nonexistent@example.com")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        void AuthController_ConfirmUser_ReturnsSuccessMessage_WhenValidToken() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                ConfirmationToken token = ConfirmationToken.builder()
                                .token("valid-token")
                                .user(savedUser)
                                .createdAt(LocalDateTime.now())
                                .expiresAt(LocalDateTime.now().plusHours(24))
                                .build();
                confirmationTokenRepository.save(token);

                ConfirmationTokenDTO confirmationRequest = ConfirmationTokenDTO.builder()
                                .token("valid-token")
                                .build();

                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/confirm")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(confirmationRequest)))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.message", CoreMatchers.is("Email token verified successfully")));
        }

        @Test
        void AuthController_ConfirmUser_Returns400_WhenInvalidToken() throws Exception {
                // Arrange
                ConfirmationTokenDTO invalidRequest = ConfirmationTokenDTO.builder()
                                .token("invalid-token")
                                .build();

                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/confirm")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void AuthController_LoginUser_ReturnsJWTResponse_WhenValidCredentials() throws Exception {
                // Arrange
                testUser.setActive(true);
                testUser.setPassword(passwordEncoder.encode("password123"));
                User savedUser = userRepository.save(testUser);
                LoginUserDTO loginRequest = LoginUserDTO.builder()
                                .username(savedUser.getUsername())
                                .password("password123")
                                .build();

                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").exists())
                                .andExpect(jsonPath("$.refreshToken").exists())
                                .andExpect(jsonPath("$.message", CoreMatchers.is("Token generated successfully!")))
                                .andExpect(jsonPath("$.user.username", CoreMatchers.is(savedUser.getUsername())))
                                .andExpect(jsonPath("$.user.email", CoreMatchers.is(savedUser.getEmail())));
        }

        @Test
        void AuthController_LoginUser_Returns401_WhenInvalidCredentials() throws Exception {
                // Arrange
                LoginUserDTO invalidRequest = LoginUserDTO.builder()
                                .username("nonexistent")
                                .password("wrongpassword")
                                .build();

                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void AuthController_RefreshToken_ReturnsJWTResponse_WhenValidRefreshToken() throws Exception {
                // Arrange
                testUser.setActive(true);
                User savedUser = userRepository.save(testUser);
                RefreshToken refreshToken = RefreshToken.builder()
                                .user(savedUser)
                                .createdAt(LocalDateTime.now())
                                .expiresAt(LocalDateTime.now().plusDays(15))
                                .build();
                RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

                RefreshTokenDTO refreshRequest = RefreshTokenDTO.builder()
                                .refreshToken(savedRefreshToken.getId().toString())
                                .build();

                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(refreshRequest)))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").exists())
                                .andExpect(jsonPath("$.refreshToken",
                                                CoreMatchers.is(savedRefreshToken.getId().toString())))
                                .andExpect(jsonPath("$.message", CoreMatchers.is("Token generated successfully!")))
                                .andExpect(jsonPath("$.user.username", CoreMatchers.is(savedUser.getUsername())));
        }

        @Test
        void AuthController_RefreshToken_Returns400_WhenInvalidRefreshToken() throws Exception {
                // Arrange
                RefreshTokenDTO invalidRequest = RefreshTokenDTO.builder()
                                .refreshToken(UUID.randomUUID().toString())
                                .build();

                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void AuthController_Logout_Returns204_WhenValidRefreshToken() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                RefreshToken refreshToken = RefreshToken.builder()
                                .user(savedUser)
                                .createdAt(LocalDateTime.now())
                                .expiresAt(LocalDateTime.now().plusDays(15))
                                .build();
                RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

                RefreshTokenDTO logoutRequest = RefreshTokenDTO.builder()
                                .refreshToken(savedRefreshToken.getId().toString())
                                .build();

                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(logoutRequest)))
                                .andExpect(status().isNoContent());
        }

        @Test
        void AuthController_Logout_Returns404_WhenInvalidRefreshToken() throws Exception {
                // Arrange
                RefreshTokenDTO invalidRequest = RefreshTokenDTO.builder()
                                .refreshToken(UUID.randomUUID().toString())
                                .build();

                // Act and Assert
                mockMvc.perform(post("/api/v1/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isNotFound());
        }
}
