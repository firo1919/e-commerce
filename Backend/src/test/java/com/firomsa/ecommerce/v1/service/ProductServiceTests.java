package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.model.Category;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.repository.CategoryRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.v1.dto.CategoryRequestDTO;
import com.firomsa.ecommerce.v1.dto.ProductRequestDTO;
import com.firomsa.ecommerce.v1.dto.ProductResponseDTO;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setup() {
        category = Category.builder().name("Electronics").build();
        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Phone")
                .description("Smart phone")
                .price(999.0)
                .stock(5)
                .active(true)
                .categories(new ArrayList<>(List.of(category)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRequestDTO = ProductRequestDTO.builder()
                .name("Phone")
                .description("Smart phone")
                .price(999.0)
                .stock(5)
                .categories(List.of(CategoryRequestDTO.builder().name("Electronics").build()))
                .build();
    }

    @Test
    public void ProductService_GetAll_ReturnsActiveProducts() {
        // Arrange
        Product inactive = Product.builder().id(UUID.randomUUID()).name("X").active(false).build();
        given(productRepository.findAll()).willReturn(List.of(product, inactive));

        // Act
        List<ProductResponseDTO> result = productService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void ProductService_GetAllInActiveProducts_ReturnsInactiveProducts() {
        // Arrange
        Product inactive = Product.builder().id(UUID.randomUUID()).name("X").active(false).build();
        given(productRepository.findAll()).willReturn(List.of(product, inactive));

        // Act
        List<ProductResponseDTO> result = productService.getAllInActiveProducts();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void ProductService_Get_ReturnsProduct() {
        // Arrange
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // Act
        ProductResponseDTO result = productService.get(product.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(product.getId().toString());
        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    public void ProductService_Get_Throws_WhenNotFound() {
        // Arrange
        given(productRepository.findById(product.getId())).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.get(product.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product: " + product.getId().toString());
    }

    @Test
    public void ProductService_Create_SavesProduct() {
        // Arrange
        given(categoryRepository.findByName("Electronics")).willReturn(Optional.of(category));
        given(productRepository.save(org.mockito.Mockito.any(Product.class))).willReturn(product);

        // Act
        ProductResponseDTO result = productService.create(productRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(product.getName());
        verify(productRepository, times(1)).save(org.mockito.Mockito.any(Product.class));
    }

    @Test
    public void ProductService_Create_Throws_WhenCategoryMissing() {
        // Arrange
        given(categoryRepository.findByName("Electronics")).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.create(productRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category: Electronics");
    }

    @Test
    public void ProductService_Update_UpdatesProduct() {
        // Arrange
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(categoryRepository.findByName("Electronics")).willReturn(Optional.of(category));
        given(productRepository.save(org.mockito.Mockito.any(Product.class))).willReturn(product);

        // Act
        ProductResponseDTO result = productService.update(productRequestDTO, product.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(product.getName());
        verify(productRepository, times(1)).save(org.mockito.Mockito.any(Product.class));
    }

    @Test
    public void ProductService_Update_Throws_WhenNotFound() {
        // Arrange
        given(productRepository.findById(product.getId())).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.update(productRequestDTO, product.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product: " + product.getId().toString());
    }

    @Test
    public void ProductService_Remove_DeletesProduct() {
        // Arrange
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // Act
        productService.remove(product.getId());

        // Assert
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    public void ProductService_Remove_Throws_WhenNotFound() {
        // Arrange
        given(productRepository.findById(product.getId())).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.remove(product.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product: " + product.getId().toString());
    }

    @Test
    public void ProductService_SoftDelete_SetsInactive() {
        // Arrange
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // Act
        productService.softDelete(product.getId());

        // Assert
        assertThat(product.isActive()).isFalse();
        verify(productRepository, times(1)).save(product);
    }

    @Test
    public void ProductService_SoftDelete_Throws_WhenNotFound() {
        // Arrange
        given(productRepository.findById(product.getId())).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.softDelete(product.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product: " + product.getId().toString());
    }

    @Test
    public void ProductService_GetReviews_ReturnsReviews() {
        // Arrange
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // Act
        var result = productService.getReviews(product.getId());

        // Assert
        assertThat(result).isNotNull();
        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    public void ProductService_GetReviews_Throws_WhenNotFound() {
        // Arrange
        given(productRepository.findById(product.getId())).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getReviews(product.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product: " + product.getId().toString());
    }
}
