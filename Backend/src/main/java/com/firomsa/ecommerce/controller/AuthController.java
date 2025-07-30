package com.firomsa.ecommerce.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firomsa.ecommerce.dto.ConfirmationTokenDTO;
import com.firomsa.ecommerce.dto.JWTResponseDTO;
import com.firomsa.ecommerce.dto.LoginUserDTO;
import com.firomsa.ecommerce.dto.RefreshTokenDTO;
import com.firomsa.ecommerce.dto.RegisterDTO;
import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.exception.ValidationException;
import com.firomsa.ecommerce.mapper.UserMapper;
import com.firomsa.ecommerce.model.RefreshToken;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.RefreshTokenRepository;
import com.firomsa.ecommerce.repository.UserRepository;
import com.firomsa.ecommerce.service.ConfirmationTokenService;
import com.firomsa.ecommerce.service.EmailService;
import com.firomsa.ecommerce.service.JWTAuthService;
import com.firomsa.ecommerce.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for performing authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTAuthService jwtAuthService;
    private final ConfirmationTokenService confirmationTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final int REFRESH_TOKEN_DURATION = 15;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
            JWTAuthService jwtAuthService, ConfirmationTokenService confirmationTokenService,
            EmailService emailService, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtAuthService = jwtAuthService;
        this.confirmationTokenService = confirmationTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Operation(summary = "For registering a user")
    @PostMapping("/register")
    public ResponseEntity<RegisterDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO user = userService.create(userRequestDTO);
        confirmationTokenService.generateToken(user.getUsername(), user.getEmail());
        RegisterDTO responseDTO = RegisterDTO.builder()
                .message("Account created successfully ,  verify email via token sent to ur email")
                .user(user)
                .build();
        return ResponseEntity.ok()
                .body(responseDTO);
    }

    @Operation(summary = "For resending confirmation token")
    @PostMapping("/resend-token")
    public ResponseEntity<RegisterDTO> resendToken(@RequestParam String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Role: USER"));
        confirmationTokenService.resendToken(user.getUsername(), user.getEmail());
        RegisterDTO responseDTO = RegisterDTO.builder()
                .message("A confirmation token is sent to your email")
                .user(UserMapper.toDTO(user))
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @Operation(summary = "For confirming a user through email")
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirmUser(
            @Valid @RequestBody ConfirmationTokenDTO confirmationTokenDTO) {
        confirmationTokenService.verifyToken(confirmationTokenDTO.getToken());
        return ResponseEntity.ok().body(Map.of("message", "Email token verified successfully"));
    }

    @Operation(summary = "For signing in a user")
    @PostMapping("/login")
    public ResponseEntity<JWTResponseDTO> loginUser(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword()));
        // generate access token
        String accessToken = jwtAuthService.generateToken(loginUserDTO.getUsername());

        // generate a refresh token
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime now = LocalDateTime.now();
        User user = (User) auth.getPrincipal();
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(now);
        refreshToken.setExpiresAt(now.plusDays(REFRESH_TOKEN_DURATION));
        refreshTokenRepository.save(refreshToken);

        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);
        JWTResponseDTO responseDTO = JWTResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getId().toString())
                .message("Token generated successfully!")
                .user(userResponseDTO)
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @Operation(summary = "For refreshing accessToken")
    @PostMapping("/refresh")
    public ResponseEntity<JWTResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByIdAndExpiresAtAfter(UUID.fromString(refreshTokenDTO.getRefreshToken()), LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("Invalid or expired refresh token"));

        String accessToken = jwtAuthService.generateToken(refreshToken.getUser().getUsername());
        JWTResponseDTO responseDTO = JWTResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getId().toString())
                .message("Token generated successfully!")
                .user(UserMapper.toDTO(refreshToken.getUser()))
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @Operation(summary = "For logging out a user")
    @PostMapping("/logout")
    public ResponseEntity<Void> revokeRefreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        refreshTokenRepository.deleteById(UUID.fromString(refreshTokenDTO.getRefreshToken()));
        return ResponseEntity.noContent().build();
    }
}
