package com.firomsa.ecommerce.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponseDTO {
    private String id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private boolean active;
}
