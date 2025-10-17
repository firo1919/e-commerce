package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.Image;

@DataJpaTest
@ActiveProfiles("test")
public class ImageRepositoryTests {

    @Autowired
    private ImageRepository imageRepository;

    private final Image testImage1 = Image.builder()
            .name("test-image-1.jpg")
            .build();

    private final Image testImage2 = Image.builder()
            .name("test-image-2.png")
            .build();

    @Test
    public void ImageRepository_Save_ReturnSavedImage() {
        // Arrange
        Image image = testImage1;

        // Act
        Image savedImage = imageRepository.save(image);

        // Assert
        assertThat(savedImage).isNotNull();
        assertThat(savedImage.getId()).isNotNull();
        assertThat(savedImage).usingRecursiveComparison().isEqualTo(image);
    }

    @Test
    public void ImageRepository_FindAll_ReturnMoreThanOneImage() {
        // Arrange
        Image image1 = testImage1;
        Image image2 = testImage2;

        // Act
        imageRepository.save(image1);
        imageRepository.save(image2);
        List<Image> savedImages = imageRepository.findAll();

        // Assert
        assertThat(savedImages).isNotNull();
        assertThat(savedImages.size()).isEqualTo(2);
        assertThat(savedImages).extracting(Image::getName).containsExactlyInAnyOrder("test-image-1.jpg",
                "test-image-2.png");
    }

    @Test
    public void ImageRepository_FindById_ReturnImage() {
        // Arrange
        Image image = testImage1;
        Image savedImage = imageRepository.save(image);

        // Act
        Optional<Image> foundImage = imageRepository.findById(savedImage.getId());

        // Assert
        assertThat(foundImage).isPresent();
        Image retrievedImage = foundImage.get();
        assertThat(retrievedImage).isNotNull();
        assertThat(retrievedImage).usingRecursiveComparison().isEqualTo(savedImage);
    }

    @Test
    public void ImageRepository_FindById_ReturnEmpty() {
        // Arrange
        Integer nonExistentId = 999;

        // Act
        Optional<Image> foundImage = imageRepository.findById(nonExistentId);

        // Assert
        assertThat(foundImage).isEmpty();
    }

    @Test
    public void ImageRepository_FindByName_ReturnImage() {
        // Arrange
        Image image = testImage1;
        imageRepository.save(image);

        // Act
        Optional<Image> foundImage = imageRepository.findByName("test-image-1.jpg");

        // Assert
        assertThat(foundImage).isPresent();
        Image retrievedImage = foundImage.get();
        assertThat(retrievedImage).isNotNull();
        assertThat(retrievedImage).usingRecursiveComparison().isEqualTo(image);
        assertThat(retrievedImage.getName()).isEqualTo("test-image-1.jpg");
    }

    @Test
    public void ImageRepository_FindByName_ReturnEmpty() {
        // Arrange
        Image image = testImage1;
        imageRepository.save(image);

        // Act
        Optional<Image> foundImage = imageRepository.findByName("nonexistent.jpg");

        // Assert
        assertThat(foundImage).isEmpty();
    }

    @Test
    public void ImageRepository_ExistsByName_ReturnTrue() {
        // Arrange
        Image image = testImage1;
        imageRepository.save(image);

        // Act
        boolean imageExists = imageRepository.existsByName("test-image-1.jpg");

        // Assert
        assertThat(imageExists).isTrue();
    }

    @Test
    public void ImageRepository_ExistsByName_ReturnFalse() {
        // Arrange
        Image image = testImage1;
        imageRepository.save(image);

        // Act
        boolean imageExists = imageRepository.existsByName("nonexistent.jpg");

        // Assert
        assertThat(imageExists).isFalse();
    }

    @Test
    public void ImageRepository_DeleteById_DeleteImage() {
        // Arrange
        Image image = testImage1;
        Image savedImage = imageRepository.save(image);

        // Act
        imageRepository.deleteById(savedImage.getId());

        // Assert
        assertThat(imageRepository.existsById(savedImage.getId())).isFalse();
    }

    @Test
    public void ImageRepository_Delete_DeleteImage() {
        // Arrange
        Image image = testImage1;
        Image savedImage = imageRepository.save(image);

        // Act
        imageRepository.delete(savedImage);

        // Assert
        assertThat(imageRepository.existsById(savedImage.getId())).isFalse();
    }
}
