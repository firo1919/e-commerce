package com.firomsa.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartResponseDTO {
    private String id;
    private String userId;
    private String productId;
    private String quantity;
    private String createdAt;
    private String updatedAt;
}
