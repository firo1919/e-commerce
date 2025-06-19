package com.firomsa.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItemResponseDTO {
    private Long id;
    private Long orderId;
    private String productId;
    private double priceAtPurchase;
    private int quantity;
}
