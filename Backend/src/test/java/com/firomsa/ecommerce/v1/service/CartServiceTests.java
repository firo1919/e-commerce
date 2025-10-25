package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
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
import com.firomsa.ecommerce.model.Cart;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.CartRepository;
import com.firomsa.ecommerce.v1.dto.CartResponseDTO;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CartServiceTests {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private User user;
    private Product product;

    @BeforeEach
    void setup() {
        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .description("A test product")
                .price(100.0)
                .stock(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        user = User.builder()
                .id(UUID.randomUUID())
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        cart = Cart.builder()
                .id(1)
                .quantity(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .product(product)
                .build();
    }

    @Test
    public void CartService_GetAll_ReturnsCarts() {
        // Arrange
        given(cartRepository.findAll()).willReturn(List.of(cart));

        // Act
        List<CartResponseDTO> result = cartService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(cartRepository, times(1)).findAll();
    }

    @Test
    public void CartService_Get_ReturnsCart() {
        // Arrange
        given(cartRepository.findById(1)).willReturn(Optional.of(cart));

        // Act
        CartResponseDTO result = cartService.get(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(cart.getId());
        verify(cartRepository, times(1)).findById(1);
    }

    @Test
    public void CartService_Get_Throws_WhenNotFound() {
        // Arrange
        given(cartRepository.findById(1)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.get(1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cart: 1");
    }

    @Test
    public void CartService_Remove_DeletesCart() {
        // Arrange
        given(cartRepository.findById(1)).willReturn(Optional.of(cart));

        // Act
        cartService.remove(1);

        // Assert
        verify(cartRepository, times(1)).delete(cart);
    }

    @Test
    public void CartService_Remove_Throws_WhenNotFound() {
        // Arrange
        given(cartRepository.findById(1)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.remove(1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cart: 1");
    }
}
