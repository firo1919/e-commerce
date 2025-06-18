package com.firomsa.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "UserName is required")
    @Size(max = 100, message = "UserName cannot exceed 100 characters")
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 100, message = "Password cannot exceed 100 characters")
    private String password;

    @NotBlank(message = "FirstName is required")
    @Size(max = 100, message = "FirstName cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "LastName is required")
    @Size(max = 100, message = "LastName cannot exceed 100 characters")
    private String lastName;
}
