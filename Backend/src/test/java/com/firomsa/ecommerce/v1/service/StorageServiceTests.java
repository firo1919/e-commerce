package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.exception.StorageException;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.repository.ImageRepository;
import com.firomsa.ecommerce.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class StorageServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private StorageService storageService;

    private Product product;

    @BeforeEach
    void setup() {
        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Product")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void StorageService_GetImage_Throws_WhenImageMissingInRepository() {
        // Arrange
        given(imageRepository.existsByName("missing.png")).willReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> storageService.getImage("missing.png"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Image: missing.png");
    }

    @Test
    public void StorageService_AddProductImage_Throws_WhenProductNotFound() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", new byte[] { 1, 2, 3 });
        given(productRepository.findById(product.getId())).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> storageService.addProductImage(file, product.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product: " + product.getId().toString());
    }

    @Test
    public void StorageService_AddProductImage_Throws_WhenFileEmpty() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", new byte[] {});
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // Act & Assert
        assertThatThrownBy(() -> storageService.addProductImage(file, product.getId()))
                .isInstanceOf(StorageException.class)
                .hasMessage("Failed to store Product image");
    }
}
