package com.firomsa.ecommerce.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firomsa.ecommerce.dto.ConfirmationTokenDTO;
import com.firomsa.ecommerce.dto.JWTResponseDTO;
import com.firomsa.ecommerce.dto.LoginUserDTO;
import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
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
    private final EmailService emailService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
            JWTAuthService jwtAuthService, ConfirmationTokenService confirmationTokenService, EmailService emailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtAuthService = jwtAuthService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
    }

    @Operation(summary = "For registering a user")
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO user = userService.create(userRequestDTO);
        String emailToken = UUID.randomUUID().toString();
        confirmationTokenService.add(emailToken, user.getUsername());
        String email = user.getEmail();
        emailService.sendSimpleMessage(email, "Account Verification", emailToken);
        return ResponseEntity.ok().body(Map.of("message", "Account created successfully ,  verify email via token sent to ur email"));
    }
    
    @Operation(summary = "For confirming a user through email")
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirmUser(@Valid @RequestBody ConfirmationTokenDTO confirmationTokenDTO) {
        confirmationTokenService.verifyToken(confirmationTokenDTO.getToken());
        return ResponseEntity.ok().body(Map.of("message", "Email token verified successfully"));
    }

    @Operation(summary = "For signing in a user")
    @PostMapping("/login")
    public ResponseEntity<JWTResponseDTO> loginUser(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword()));
        String token = jwtAuthService.generateToken(loginUserDTO.getUsername());
        JWTResponseDTO responseDTO = JWTResponseDTO.builder()
                .token(token)
                .message("Token generated successfully!")
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }
}
