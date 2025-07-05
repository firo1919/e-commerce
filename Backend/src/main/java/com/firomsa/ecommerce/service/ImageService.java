package com.firomsa.ecommerce.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.dto.ImageDTO;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.mapper.ImageMapper;
import com.firomsa.ecommerce.model.Image;
import com.firomsa.ecommerce.repository.ImageRepository;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ImageDTO> getAll() {
        return imageRepository.findAll().stream().map(ImageMapper::toDTO).toList();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ImageDTO get(String name) {
        Image image = imageRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Image: " + name));
        return ImageMapper.toDTO(image);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void remove(String name) {
        Image image = imageRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Image: " +
                        name));
        imageRepository.delete(image);
    }
}
