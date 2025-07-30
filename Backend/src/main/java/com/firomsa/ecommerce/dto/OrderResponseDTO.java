package com.firomsa.ecommerce.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderResponseDTO {
    private Long id;
    private String userId;
    private List<OrderItemResponseDTO> orderItems;
    private String status;
    private Double totalPrice;
    private String txRef;
    private String createdAt;
    private String updatedAt;
}
