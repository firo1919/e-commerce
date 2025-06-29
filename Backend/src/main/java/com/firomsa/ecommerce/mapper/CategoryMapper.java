package com.firomsa.ecommerce.mapper;

import com.firomsa.ecommerce.dto.CategoryRequestDTO;
import com.firomsa.ecommerce.dto.CategoryResponseDTO;
import com.firomsa.ecommerce.model.Category;

public class CategoryMapper {

    private CategoryMapper(){}

    public static CategoryResponseDTO toDTO(Category category){
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toModel(CategoryRequestDTO categoryRequestDTO){
        return Category.builder()
                .name(categoryRequestDTO.getName())
                .build();
    }
}
