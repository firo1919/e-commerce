package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.Product;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    private final Product testProduct1 = Product.builder()
            .name("Test Product 1")
            .description("A test product")
            .price(100.0)
            .stock(10)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    private final Product testProduct2 = Product.builder()
            .name("Test Product 2")
            .description("Another test product")
            .price(200.0)
            .stock(5)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    @Test
    public void ProductRepository_Save_ReturnSavedProduct() {
        // Arrange
        Product product = testProduct1;

        // Act
        Product savedProduct = productRepository.save(product);

        // Assert
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct).usingRecursiveComparison().isEqualTo(product);
        assertThat(savedProduct.getCreatedAt()).isNotNull();
        assertThat(savedProduct.getUpdatedAt()).isNotNull();
    }

    @Test
    public void ProductRepository_FindAll_ReturnMoreThanOneProduct() {
        // Arrange
        Product product1 = testProduct1;
        Product product2 = testProduct2;

        // Act
        productRepository.save(product1);
        productRepository.save(product2);
        List<Product> savedProducts = productRepository.findAll();

        // Assert
        assertThat(savedProducts).isNotNull();
        assertThat(savedProducts.size()).isEqualTo(2);
        assertThat(savedProducts).extracting(Product::getName).containsExactlyInAnyOrder("Test Product 1",
                "Test Product 2");
        assertThat(savedProducts).extracting(Product::getPrice).containsExactlyInAnyOrder(100.0, 200.0);
    }

    @Test
    public void ProductRepository_FindById_ReturnProduct() {
        // Arrange
        Product product = testProduct1;
        Product savedProduct = productRepository.save(product);

        // Act
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // Assert
        assertThat(foundProduct).isPresent();
        Product retrievedProduct = foundProduct.get();
        assertThat(retrievedProduct).isNotNull();
        assertThat(retrievedProduct).usingRecursiveComparison().isEqualTo(savedProduct);
    }

    @Test
    public void ProductRepository_FindById_ReturnEmpty() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<Product> foundProduct = productRepository.findById(nonExistentId);

        // Assert
        assertThat(foundProduct).isEmpty();
    }

    @Test
    public void ProductRepository_DeleteById_DeleteProduct() {
        // Arrange
        Product product = testProduct1;
        Product savedProduct = productRepository.save(product);

        // Act
        productRepository.deleteById(savedProduct.getId());

        // Assert
        assertThat(productRepository.existsById(savedProduct.getId())).isFalse();
    }

    @Test
    public void ProductRepository_Delete_DeleteProduct() {
        // Arrange
        Product product = testProduct1;
        Product savedProduct = productRepository.save(product);

        // Act
        productRepository.delete(savedProduct);

        // Assert
        assertThat(productRepository.existsById(savedProduct.getId())).isFalse();
    }
}
