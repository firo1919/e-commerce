package com.firomsa.ecommerce.v1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartRequestDTO {
    @NotNull(message = "quantity is required")
    @Min(1)
    private int quantity;
}
