package com.firomsa.ecommerce.v1.mapper;

import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.v1.dto.AddressRequestDTO;
import com.firomsa.ecommerce.v1.dto.AddressResponseDTO;

public class AddressMapper {

    private AddressMapper() {
    }

    public static AddressResponseDTO toDTO(Address address) {
        return AddressResponseDTO.builder()
                .id(address.getId())
                .userId(address.getUser().getId().toString())
                .firstName(address.getFirstName())
                .lastName(address.getLastName())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .country(address.getCountry())
                .phone(address.getPhone())
                .active(address.getActive())
                .build();
    }

    public static Address toModel(AddressRequestDTO addressRequestDTO) {
        return Address.builder()
                .firstName(addressRequestDTO.getFirstName())
                .lastName(addressRequestDTO.getLastName())
                .street(addressRequestDTO.getStreet())
                .city(addressRequestDTO.getCity())
                .state(addressRequestDTO.getState())
                .zipCode(addressRequestDTO.getZipCode())
                .country(addressRequestDTO.getCountry())
                .phone(addressRequestDTO.getPhone())
                .active(addressRequestDTO.isActive())
                .build();
    }
}
