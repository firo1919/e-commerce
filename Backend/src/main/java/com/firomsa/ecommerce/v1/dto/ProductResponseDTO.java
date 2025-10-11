package com.firomsa.ecommerce.v1.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductResponseDTO {
    private String id;
    private String name;
    private String description;
    private Double price;
    private int stock;
    private List<CategoryResponseDTO> categories;
    private List<ImageDTO> productImages;
    private boolean active;
}
