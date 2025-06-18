package com.firomsa.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddressRequestDTO {
    @NotBlank(message = "firstname is required")
    @Size(max = 100, message = "firstname cannot exceed 100 characters")
    private String firstName;
    @NotBlank(message = "lastname is required")
    @Size(max = 100, message = "lastname cannot exceed 100 characters")
    private String lastName;
    @NotBlank(message = "street is required")
    @Size(max = 100, message = "street cannot exceed 100 characters")
    private String street;
    @NotBlank(message = "city is required")
    @Size(max = 100, message = "city cannot exceed 100 characters")
    private String city;
    @NotBlank(message = "state is required")
    private String state;
    @NotBlank(message = "zipcode is required")
    private String zipCode;
    @NotBlank(message = "country is required")
    private String country;
    @NotBlank(message = "phone is required")
    private String phone;
    private Boolean active;
}
