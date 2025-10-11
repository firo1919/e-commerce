package com.firomsa.ecommerce.v1.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductRequestDTO {
    @NotBlank(message = "product name is required")
    private String name;

    @NotBlank(message = "product description is required")
    private String description;

    @NotNull(message = "product price is required")
    @Min(0)
    private Double price;

    @NotNull(message = "product stock is required")
    @Min(0)
    private int stock;

    @NotNull(message = "product categories is required")
    private List<CategoryRequestDTO> categories;
}
