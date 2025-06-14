package com.firomsa.ecommerce.mapper;

import java.time.LocalDateTime;

import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

public class UserMapper {
    public static UserResponseDTO toDTO(User user){
        return UserResponseDTO.builder()
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .id(user.getId().toString())
            .lastName(user.getLastName())
            .userName(user.getUserName())
            .role(user.getRole().toString())
            .isActive(Boolean.toString(user.isActive()))
            .build();
    }

    public static User toModel(UserRequestDTO userRequestDTO){
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
            .lastName(userRequestDTO.getLastName())
            .firstName(userRequestDTO.getFirstName())
            .userName(userRequestDTO.getUserName())
            .email(userRequestDTO.getEmail())
            .password(userRequestDTO.getPassword())
            .createdAt(now)
            .updatedAt(now)
            .build();
    }
}
