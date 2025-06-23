package com.firomsa.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firomsa.ecommerce.dto.AddressRequestDTO;
import com.firomsa.ecommerce.dto.AddressResponseDTO;
import com.firomsa.ecommerce.service.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Address", description = "API for managing addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "For getting all addresses")
    @GetMapping()
    public ResponseEntity<List<AddressResponseDTO>> getAllCarts() {
        List<AddressResponseDTO> addresses = addressService.getAll();
        return ResponseEntity.ok().body(addresses);
    }

    @Operation(summary = "For getting a single address")
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getCart(@PathVariable int id) {
        AddressResponseDTO address = addressService.get(id);
        return ResponseEntity.ok().body(address);
    }

    @Operation(summary = "For updating an address")
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@Valid @RequestBody AddressRequestDTO addressRequestDTO, @PathVariable int id) {
        AddressResponseDTO address = addressService.update(addressRequestDTO, id);
        return ResponseEntity.ok().body(address);
    }

    @Operation(summary = "For deleting a address")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable int id) {
        addressService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
