package com.firomsa.ecommerce.v1.mapper;

import com.firomsa.ecommerce.model.Category;
import com.firomsa.ecommerce.v1.dto.CategoryRequestDTO;
import com.firomsa.ecommerce.v1.dto.CategoryResponseDTO;

public class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponseDTO toDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toModel(CategoryRequestDTO categoryRequestDTO) {
        return Category.builder()
                .name(categoryRequestDTO.getName())
                .build();
    }
}
