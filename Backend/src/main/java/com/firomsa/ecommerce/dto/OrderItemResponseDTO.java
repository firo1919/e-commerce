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
    private String id;
    private String orderId;
    private String productId;
    private String priceAtPurchase;
    private String quantity;
}
