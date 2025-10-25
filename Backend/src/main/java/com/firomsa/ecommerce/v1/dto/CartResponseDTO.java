package com.firomsa.ecommerce.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartResponseDTO {
    private Integer id;
    private String userId;
    private String productId;
    private int quantity;
    private String createdAt;
    private String updatedAt;
}
