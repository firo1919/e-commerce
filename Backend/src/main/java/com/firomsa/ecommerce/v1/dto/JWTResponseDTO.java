package com.firomsa.ecommerce.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JWTResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String message;
    private UserResponseDTO user;
}
