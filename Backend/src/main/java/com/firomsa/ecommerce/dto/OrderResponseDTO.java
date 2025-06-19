package com.firomsa.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private String createdAt;
    private String updatedAt;
}
