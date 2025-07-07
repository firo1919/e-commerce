package com.firomsa.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConfirmationTokenDTO {
    @NotNull
    private String token;

    @NotNull
    private LoginUserDTO user;
}
