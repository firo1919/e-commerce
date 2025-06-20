package com.firomsa.ecommerce.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firomsa.ecommerce.dto.ImageDTO;
import com.firomsa.ecommerce.service.ImageService;
import com.firomsa.ecommerce.service.StorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Image", description = "api for managing images")
public class ImageController {

    private final StorageService storageService;
    private ImageService imageService;

    public ImageController(ImageService imageService, StorageService storageService){
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
}
