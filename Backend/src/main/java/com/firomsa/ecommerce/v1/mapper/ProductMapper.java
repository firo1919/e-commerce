package com.firomsa.ecommerce.v1.mapper;

import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.v1.dto.ProductRequestDTO;
import com.firomsa.ecommerce.v1.dto.ProductResponseDTO;

public class ProductMapper {

    private ProductMapper() {
    }

    public static ProductResponseDTO toDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categories(product.getCategories().stream().map(CategoryMapper::toDTO).toList())
                .active(product.isActive())
                .productImages(product.getProductImages().stream().map(ImageMapper::toDTO).toList())
                .build();
    }

    public static Product toModel(ProductRequestDTO productRequestDTO) {
        return Product.builder()
                .name(productRequestDTO.getName())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice())
                .stock(productRequestDTO.getStock())
                .build();
    }
}
