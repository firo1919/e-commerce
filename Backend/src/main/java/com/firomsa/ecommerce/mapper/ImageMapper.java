package com.firomsa.ecommerce.mapper;

import com.firomsa.ecommerce.dto.ImageDTO;
import com.firomsa.ecommerce.model.Image;

public class ImageMapper {
    public static ImageDTO toDTO(Image image){
        return ImageDTO.builder()
                .name(image.getName())
                .url("/api/images/"+image.getName())
                .build();
    }
}
