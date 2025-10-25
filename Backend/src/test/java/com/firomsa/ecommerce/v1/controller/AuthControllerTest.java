package com.firomsa.ecommerce.v1.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.firomsa.ecommerce.model.RefreshToken;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.RefreshTokenRepository;
import com.firomsa.ecommerce.repository.UserRepository;
import com.firomsa.ecommerce.security.JWTSecurityFilter;
import com.firomsa.ecommerce.v1.dto.ConfirmationTokenDTO;
import com.firomsa.ecommerce.v1.dto.LoginUserDTO;
import com.firomsa.ecommerce.v1.dto.RefreshTokenDTO;
import com.firomsa.ecommerce.v1.dto.UserRequestDTO;
import com.firomsa.ecommerce.v1.dto.UserResponseDTO;
import com.firomsa.ecommerce.v1.mapper.UserMapper;
import com.firomsa.ecommerce.v1.service.ConfirmationTokenService;
import com.firomsa.ecommerce.v1.service.EmailService;
import com.firomsa.ecommerce.v1.service.JWTAuthService;
import com.firomsa.ecommerce.v1.service.UserService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

        @MockitoBean
        private AuthenticationManager authenticationManager;

        @MockitoBean
        private UserService userService;

        @MockitoBean
        private JWTAuthService jwtAuthService;

        @MockitoBean
        private JWTSecurityFilter jwtSecurityFilter;

        @MockitoBean
        private EmailService emailService;

        @MockitoBean
        private ConfirmationTokenService confirmationTokenService;

        @MockitoBean
        private RefreshTokenRepository refreshTokenRepository;

        @MockitoBean
        private UserRepository userRepository;

        @Autowired
        private MockMvc mockMvc;

        private Role role;
        private User user;
        private RefreshToken refreshToken;

        @BeforeEach
        void setup() {
                role = Role.builder().name("USER").id(1).build();
                user = User.builder()
                                .id(UUID.randomUUID())
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
                refreshToken = RefreshToken.builder().id(UUID.randomUUID()).user(user).createdAt(LocalDateTime.now())
                                .expiresAt(LocalDateTime.now().plusDays(10)).build();
        }

        @Test
        void registerUser_returnsOkAndTriggersToken() throws Exception {
                UserRequestDTO req = UserRequestDTO.builder().username("firo").email("example@gmail.com")
                                .firstName("Firomsa")
                                .lastName("Assefa").password("123")
                                .build();
                UserResponseDTO res = UserMapper.toDTO(user);
                given(userService.create(Mockito.any(UserRequestDTO.class))).willReturn(res);

                mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(req)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.user.username", CoreMatchers.is(res.getUsername())));

                verify(confirmationTokenService, times(1)).generateToken(res.getUsername(), res.getEmail());
        }

        @Test
        void resendToken_returnsOk() throws Exception {
                given(userRepository.findByEmail("example@gmail.com")).willReturn(Optional.of(user));
                mockMvc.perform(post("/api/v1/auth/resend-token").param("email", "example@gmail.com"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.user.username", CoreMatchers.is(user.getUsername())));
                verify(confirmationTokenService, times(1)).resendToken(user.getUsername(), user.getEmail());
        }

        @Test
        void confirmUser_returnsOk() throws Exception {
                ConfirmationTokenDTO dto = new ConfirmationTokenDTO();
                dto.setToken("123456");
                mockMvc.perform(post("/api/v1/auth/confirm").contentType(MediaType.APPLICATION_JSON)
                                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message", CoreMatchers.is("Email token verified successfully")));
                verify(confirmationTokenService, times(1)).verifyToken("123456");
        }

        @Test
        void loginUser_returnsTokens() throws Exception {
                LoginUserDTO login = new LoginUserDTO();
                login.setUsername("firo");
                login.setPassword("123");

                Authentication auth = new UsernamePasswordAuthenticationToken(user, null, java.util.List.of());
                given(authenticationManager.authenticate(Mockito.any())).willReturn(auth);
                given(jwtAuthService.generateToken("firo")).willReturn("access");
                given(refreshTokenRepository.save(Mockito.any(RefreshToken.class))).willAnswer(inv -> {
                        RefreshToken t = inv.getArgument(0);
                        t.setId(UUID.randomUUID());
                        return t;
                });

                mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(login)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken", CoreMatchers.is("access")))
                                .andExpect(jsonPath("$.user.username", CoreMatchers.is("firo")));

                verify(jwtAuthService, times(1)).generateToken("firo");
                verify(refreshTokenRepository, times(1)).save(Mockito.any(RefreshToken.class));
        }

        @Test
        void refreshToken_returnsNewAccessToken() throws Exception {
                RefreshToken t = new RefreshToken();
                t.setId(UUID.randomUUID());
                t.setUser(user);
                t.setCreatedAt(LocalDateTime.now());
                t.setExpiresAt(LocalDateTime.now().plusDays(10));
                given(refreshTokenRepository.findByIdAndExpiresAtAfter(Mockito.any(UUID.class),
                                Mockito.any(LocalDateTime.class)))
                                .willReturn(Optional.of(t));
                given(jwtAuthService.generateToken(user.getUsername())).willReturn("newAccess");

                RefreshTokenDTO dto = new RefreshTokenDTO();
                dto.setRefreshToken(t.getId().toString());
                mockMvc.perform(post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken", CoreMatchers.is("newAccess")));
        }

        @Test
        void refreshToken_invalid_throwsValidation() throws Exception {
                given(refreshTokenRepository.findByIdAndExpiresAtAfter(Mockito.any(UUID.class),
                                Mockito.any(LocalDateTime.class)))
                                .willReturn(Optional.empty());

                RefreshTokenDTO dto = new RefreshTokenDTO();
                dto.setRefreshToken(UUID.randomUUID().toString());
                mockMvc.perform(post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto)))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        void logout_revokesToken() throws Exception {
                RefreshTokenDTO dto = new RefreshTokenDTO();
                UUID id = UUID.randomUUID();
                dto.setRefreshToken(id.toString());
                given(refreshTokenRepository.findById(Mockito.any(UUID.class)))
                                .willReturn(Optional.of(refreshToken));

                mockMvc.perform(post("/api/v1/auth/logout").contentType(MediaType.APPLICATION_JSON)
                                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto)))
                                .andDo(print())
                                .andExpect(status().isNoContent());
                verify(refreshTokenRepository, times(1)).delete(refreshToken);
        }
}
