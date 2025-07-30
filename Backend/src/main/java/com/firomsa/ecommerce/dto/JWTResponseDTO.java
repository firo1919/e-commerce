package com.firomsa.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JWTResponseDTO {
    private String token;
    private String message;
    private UserResponseDTO user;
}
