package com.firomsa.ecommerce.controller;

import java.net.URI;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.firomsa.ecommerce.dto.ConfirmationTokenDTO;
import com.firomsa.ecommerce.dto.JWTResponseDTO;
import com.firomsa.ecommerce.dto.LoginUserDTO;
import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.model.ConfirmationToken;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.service.ConfirmationTokenService;
import com.firomsa.ecommerce.service.EmailService;
import com.firomsa.ecommerce.service.JWTAuthService;
import com.firomsa.ecommerce.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
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
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO user = userService.create(userRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{id}")
                .buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(user);
    }

    @Operation(summary = "For signing in a user")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        Authentication authentication =  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword()));
        String emailToken = UUID.randomUUID().toString();
        User user = (User) authentication.getPrincipal();
        ConfirmationToken token = ConfirmationToken.builder().token(emailToken).user(user).build();
        confirmationTokenService.add(token);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/auth/confirm")
                .build().toUri();
        String email = user.getEmail();
        emailService.sendSimpleMessage(email, "Account Verification", emailToken);
        return ResponseEntity.created(location).body(Map.of("message", "please verify with your token, a token is sent to ur email"));
    }

    @Operation(summary = "For confirming a user through email")
    @PostMapping("/confirm")
    public ResponseEntity<JWTResponseDTO> confirmUser(@Valid @RequestBody ConfirmationTokenDTO confirmationTokenDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(confirmationTokenDTO.getUser().getUsername(), confirmationTokenDTO.getUser().getPassword()));
        confirmationTokenService.verifyToken(confirmationTokenDTO.getToken());
        String token = jwtAuthService.generateToken(confirmationTokenDTO.getUser().getUsername());
        JWTResponseDTO responseDTO = JWTResponseDTO.builder()
                .token(token)
                .message("Token generated successfully!")
                .build();
        return ResponseEntity.ok()
                .body(responseDTO);
    }
}
