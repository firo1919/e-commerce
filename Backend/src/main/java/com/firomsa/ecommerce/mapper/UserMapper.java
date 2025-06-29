package com.firomsa.ecommerce.mapper;

import java.time.LocalDateTime;

import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.model.User;

public class UserMapper {
    public static UserResponseDTO toDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .role(user.getRole().getName())
                .active(user.isActive())
                .build();
    }

    public static User toModel(UserRequestDTO userRequestDTO) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .lastName(userRequestDTO.getLastName())
                .firstName(userRequestDTO.getFirstName())
                .username(userRequestDTO.getUsername())
                .email(userRequestDTO.getEmail())
                .password(userRequestDTO.getPassword())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
