package com.firomsa.ecommerce.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.firomsa.ecommerce.dto.ImageDTO;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.exception.StorageException;
import com.firomsa.ecommerce.mapper.ImageMapper;
import com.firomsa.ecommerce.model.Image;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.repository.ImageRepository;
import com.firomsa.ecommerce.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StorageService {

    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final Path rootLocation;

    public StorageService(ProductRepository productRepository, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.rootLocation = Paths.get("/store");
    }

    public List<ImageDTO> getProductImages(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + id.toString()));
        List<Image> images = product.getProductImages();
        return images.stream().map(ImageMapper::toDTO).toList();
    }

    public Resource getImage(String imageName) {
        if(!imageRepository.existsByName(imageName)){
            throw new ResourceNotFoundException("Image: "+imageName);
        }
        try {
            Path file = this.rootLocation.resolve(imageName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Could not read file: " + imageName);
            }
        } catch (Exception e) {
            throw new StorageException("Could not read file: " + imageName, e);
        }
    }
    

    public ImageDTO addProductImage(MultipartFile file, UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + id.toString()));
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store Product image");
            }
            log.info("filename" + file.getOriginalFilename());
            String filetype = new StringBuilder(
                    new StringBuilder(file.getOriginalFilename())
                            .reverse()
                            .toString()
                            .split("\\.")[0])
                    .reverse().toString();

            String fileName = "image-" + LocalDateTime.now().toString() + "." + filetype;
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(fileName))
                    .normalize().toAbsolutePath();

            log.info("adding image file storage");

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            Image image = Image.builder()
                    .name(fileName)
                    .product(product)
                    .build();

            log.info("adding image entity to database");

            Image savedImage = imageRepository.save(image);
            return ImageMapper.toDTO(savedImage);

        } catch (IOException e) {
            throw new StorageException("Failed to store Product image", e);
        }

    }

}
