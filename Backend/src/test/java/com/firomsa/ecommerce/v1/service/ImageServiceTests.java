package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.model.Image;
import com.firomsa.ecommerce.repository.ImageRepository;
import com.firomsa.ecommerce.v1.dto.ImageDTO;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ImageServiceTests {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    @Test
    public void ImageService_GetAll_ReturnsImages() {
        // Arrange
        Image image = Image.builder().id(1).name("img.png").build();
        given(imageRepository.findAll()).willReturn(List.of(image));

        // Act
        List<ImageDTO> result = imageService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(imageRepository, times(1)).findAll();
    }

    @Test
    public void ImageService_Get_ReturnsImage() {
        // Arrange
        Image image = Image.builder().id(1).name("img.png").build();
        given(imageRepository.findByName("img.png")).willReturn(Optional.of(image));

        // Act
        ImageDTO result = imageService.get("img.png");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("img.png");
        verify(imageRepository, times(1)).findByName("img.png");
    }

    @Test
    public void ImageService_Get_Throws_WhenNotFound() {
        // Arrange
        given(imageRepository.findByName("img.png")).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> imageService.get("img.png"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Image: img.png");
    }

    @Test
    public void ImageService_Remove_DeletesImage() {
        // Arrange
        Image image = Image.builder().id(1).name("img.png").build();
        given(imageRepository.findByName("img.png")).willReturn(Optional.of(image));

        // Act
        imageService.remove("img.png");

        // Assert
        verify(imageRepository, times(1)).delete(image);
    }

    @Test
    public void ImageService_Remove_Throws_WhenNotFound() {
        // Arrange
        given(imageRepository.findByName("img.png")).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> imageService.remove("img.png"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Image: img.png");
    }
}
