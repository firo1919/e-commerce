package com.firomsa.ecommerce.v1.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firomsa.ecommerce.v1.dto.ImageDTO;
import com.firomsa.ecommerce.v1.service.ImageService;
import com.firomsa.ecommerce.v1.service.StorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/images")
@Tag(name = "Image", description = "api for managing images")
public class ImageController {

    private final StorageService storageService;
    private final ImageService imageService;

    public ImageController(ImageService imageService, StorageService storageService) {
        this.imageService = imageService;
        this.storageService = storageService;
    }

    @Operation(summary = "For getting all images")
    @GetMapping()
    public ResponseEntity<List<ImageDTO>> getAllImages() {
        List<ImageDTO> images = imageService.getAll();
        return ResponseEntity.ok().body(images);
    }

    @Operation(summary = "For getting a single image")
    @GetMapping(path = "/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable String id) {
        Resource resource = storageService.getImage(id);
        return ResponseEntity.ok().body(resource);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "For deleting an image")
    public ResponseEntity<Void> deleteImage(@PathVariable String id) {
        imageService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
