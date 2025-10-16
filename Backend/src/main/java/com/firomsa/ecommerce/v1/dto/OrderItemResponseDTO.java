package com.firomsa.ecommerce.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItemResponseDTO {
    private Integer id;
    private Integer orderId;
    private String productId;
    private double priceAtPurchase;
    private int quantity;
}
