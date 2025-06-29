package com.firomsa.ecommerce.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

class UserMapperTest {

    @Test
    public void UserMapper_ToDTO_ReturnUserResponseDTO(){
        User user = User.builder()
            .id(UUID.randomUUID())
            .username("firo")
            .email("example@gmail.com")
            .firstName("Firomsa")
            .lastName("Assefa")
            .password("123")
            .role(Role.builder().name("USER").id(1).build())
            .active(true)
            .build();

        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        assertEquals(user.getId().toString(), userResponseDTO.getId());
        assertEquals(user.getUsername(), userResponseDTO.getUsername());
        assertEquals(user.getEmail(), userResponseDTO.getEmail());
        assertEquals(user.getFirstName(), userResponseDTO.getFirstName());
        assertEquals(user.getLastName(), userResponseDTO.getLastName());
    }

    @Test
    public void UserMapper_ToModel_ReturnUser() {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .build();

        User user = UserMapper.toModel(userRequestDTO);

        assertEquals(user.getEmail(), userRequestDTO.getEmail());
        assertEquals(user.getUsername(), userRequestDTO.getUsername());
        assertEquals(user.getFirstName(), userRequestDTO.getFirstName());
        assertEquals(user.getLastName(), userRequestDTO.getLastName());
        assertEquals(user.getPassword(), userRequestDTO.getPassword());
    }
}