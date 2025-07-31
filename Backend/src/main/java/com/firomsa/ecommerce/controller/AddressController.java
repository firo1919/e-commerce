package com.firomsa.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firomsa.ecommerce.dto.AddressResponseDTO;
import com.firomsa.ecommerce.service.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
}
