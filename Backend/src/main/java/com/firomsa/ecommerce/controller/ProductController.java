package com.firomsa.ecommerce.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.firomsa.ecommerce.dto.ImageDTO;
import com.firomsa.ecommerce.dto.ProductRequestDTO;
import com.firomsa.ecommerce.dto.ProductResponseDTO;
import com.firomsa.ecommerce.service.ProductService;
import com.firomsa.ecommerce.service.StorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "api for managing products")
public class ProductController {
    private final ProductService productService;
    private final StorageService storageService;

    public ProductController(ProductService productService, StorageService storageService) {
        this.productService = productService;
        this.storageService = storageService;
    }

    @Operation(summary = "For getting all products")
    @GetMapping()
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAll();
        return ResponseEntity.ok().body(products);
    }

    @Operation(summary = "For getting a single product")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable UUID id) {
        ProductResponseDTO product = productService.get(id);
        return ResponseEntity.ok().body(product);
    }

    @Operation(summary = "For adding a product")
    @PostMapping()
    public ResponseEntity<ProductResponseDTO> addProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO product = productService.create(productRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/products/{id}")
                .buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(location).body(product);
    }

    @Operation(summary = "For updating a product")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO,
            @PathVariable UUID id) {
        ProductResponseDTO product = productService.update(productRequestDTO, id);
        return ResponseEntity.ok().body(product);
    }

    @Operation(summary = "For deleting a product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id, @RequestParam Optional<Boolean> force) {
        if (force.isPresent() && force.get() == true) {
            productService.remove(id);
        } else {
            productService.softDelete(id);
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "For getting all product images")
    @GetMapping("/{id}/productImages")
    public ResponseEntity<List<ImageDTO>> getAllProductImages(@PathVariable UUID id) {
        List<ImageDTO> images = storageService.getProductImages(id);
        return ResponseEntity.ok().body(images);
    }

    @Operation(summary = "For adding a product image")
    @PostMapping(path = "/{id}/productImages", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<ImageDTO> uploadProductImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        ImageDTO image = storageService.addProductImage(file, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(image);
	}
}
