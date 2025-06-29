package com.firomsa.ecommerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firomsa.ecommerce.dto.AddressRequestDTO;
import com.firomsa.ecommerce.dto.AddressResponseDTO;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.mapper.AddressMapper;
import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.repository.AddressRepository;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository){
        this.addressRepository = addressRepository;
    }

    public List<AddressResponseDTO> getAll() {
        return addressRepository.findAll().stream().map(AddressMapper::toDTO).toList();
    }

    public AddressResponseDTO get(int id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address: " + id));
        return AddressMapper.toDTO(address);
    }

    public void remove(int id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address: " +
                        id));
        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponseDTO update(AddressRequestDTO addressRequestDTO, int id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address: " + id));
        address.setFirstName(addressRequestDTO.getFirstName());
        address.setLastName(addressRequestDTO.getLastName());
        address.setState(addressRequestDTO.getState());
        address.setCity(addressRequestDTO.getCity());
        address.setStreet(addressRequestDTO.getStreet());
        address.setZipCode(addressRequestDTO.getZipCode());
        address.setCountry(addressRequestDTO.getCountry());
        address.setPhone(addressRequestDTO.getPhone());
        address.setActive(addressRequestDTO.isActive());
        address.setUpdatedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(address.getActive())) {
            Optional<Address> defaultAddress = addressRepository.findByUserAndActive(address.getUser(), true);
            if (defaultAddress.isPresent() && defaultAddress.get().getId().equals(address.getId())) {
                Address defAddress = defaultAddress.get();
                defAddress.setActive(false);
                addressRepository.save(defAddress);
            }
        }

        return AddressMapper.toDTO(addressRepository.save(address));
    }
}
