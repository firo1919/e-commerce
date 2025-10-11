package com.firomsa.ecommerce.v1.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.repository.AddressRepository;
import com.firomsa.ecommerce.v1.dto.AddressResponseDTO;
import com.firomsa.ecommerce.v1.mapper.AddressMapper;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<AddressResponseDTO> getAll() {
        return addressRepository.findAll().stream().map(AddressMapper::toDTO).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AddressResponseDTO get(int id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address: " + id));
        return AddressMapper.toDTO(address);
    }
}
