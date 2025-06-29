package com.firomsa.ecommerce.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.firomsa.ecommerce.dto.JWTResponseDTO;
import com.firomsa.ecommerce.dto.LoginUserDTO;
import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
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

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
            JWTAuthService jwtAuthService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtAuthService = jwtAuthService;
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
    public ResponseEntity<JWTResponseDTO> loginUser(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword()));
        String token = jwtAuthService.generateToken(loginUserDTO.getUsername());
        JWTResponseDTO responseDTO = JWTResponseDTO.builder()
                .token(token)
                .message("Token generated successfully!")
                .build();
        return ResponseEntity.ok()
                .body(responseDTO);
    }
}
