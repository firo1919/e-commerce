package com.firomsa.ecommerce.v1.dto;

import com.yaphet.chapa.model.InitializeResponseData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDetailDTO {
    private OrderResponseDTO order;
    private InitializeResponseData response;
    private AddressResponseDTO address;
}
