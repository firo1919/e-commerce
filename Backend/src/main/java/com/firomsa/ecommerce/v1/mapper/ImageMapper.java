package com.firomsa.ecommerce.v1.mapper;

import com.firomsa.ecommerce.model.Image;
import com.firomsa.ecommerce.v1.dto.ImageDTO;

public class ImageMapper {

    private ImageMapper() {
    }

    public static ImageDTO toDTO(Image image) {
        return ImageDTO.builder()
                .name(image.getName())
                .url("/api/images/" + image.getName())
                .build();
    }
}
