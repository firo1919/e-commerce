package com.firomsa.ecommerce.mapper;

import java.time.LocalDateTime;

import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

public class UserMapper {
    public static UserResponseDTO toDTO(User user){
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setId(user.getId().toString());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setUserName(user.getUserName());
        userResponseDTO.setRole(user.getRole().toString());
        userResponseDTO.setIsActive(Boolean.toString(user.isActive()));
        userResponseDTO.setPassword(user.getPassword());
        return userResponseDTO;
    }

    public static User toModel(UserRequestDTO userRequestDTO){
        User user = new User();
        user.setLastName(userRequestDTO.getLastName());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setUserName(userRequestDTO.getUserName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        user.setActive(Boolean.parseBoolean(userRequestDTO.getIsActive()));
        user.setRole(Enum.valueOf(Role.class, userRequestDTO.getRole()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}
