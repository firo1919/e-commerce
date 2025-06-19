package com.firomsa.ecommerce.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartRequestDTO {
    @NotBlank(message = "quantity is required")
    @Min(1)
    private int quantity;
    @NotBlank(message = "productId is required")
    private UUID productId;
}
