package com.firomsa.ecommerce.mapper;

import com.firomsa.ecommerce.dto.ProductRequestDTO;
import com.firomsa.ecommerce.dto.ProductResponseDTO;
import com.firomsa.ecommerce.model.Product;

public class ProductMapper {
    public static ProductResponseDTO toDTO(Product product){
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

    public static Product toModel(ProductRequestDTO productRequestDTO){
        return Product.builder()
                .name(productRequestDTO.getName())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice())
                .stock(productRequestDTO.getStock())
                .build();
    }
}
