package com.firomsa.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponseDTO {
    private String id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String role;
    private String isActive;
}
