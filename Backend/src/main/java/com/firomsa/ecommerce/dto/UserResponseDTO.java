package com.firomsa.ecommerce.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserResponseDTO {
    private String id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String isActive;
}
