package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.firomsa.ecommerce.exception.OrderProcessException;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.model.OrderItem;
import com.firomsa.ecommerce.model.OrderStatus;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.OrderRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.v1.dto.OrderResponseDTO;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Product product;
    private User user;

    @BeforeEach
    void setup() {
        product = Product.builder().name("P").stock(5).id(UUID.randomUUID()).build();
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
        order = Order.builder()
                .id(1)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .priceAtPurchase(10D)
                .order(order)
                .build();

        order.setOrderItems(List.of(item));
    }

    @Test
    public void OrderService_GetAll_ReturnsOrders() {
        // Arrange
        given(orderRepository.findAll()).willReturn(List.of(order));

        // Act
        List<OrderResponseDTO> result = orderService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void OrderService_Get_ReturnsOrder() {
        // Arrange
        given(orderRepository.findById(1)).willReturn(Optional.of(order));

        // Act
        OrderResponseDTO result = orderService.get(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(order.getId());
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    public void OrderService_Get_Throws_WhenNotFound() {
        // Arrange
        given(orderRepository.findById(1)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.get(1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order: 1");
    }

    @Test
    public void OrderService_Remove_DeletesOrder() {
        // Arrange
        given(orderRepository.findById(1)).willReturn(Optional.of(order));

        // Act
        orderService.remove(1);

        // Assert
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    public void OrderService_Remove_Throws_WhenNotFound() {
        // Arrange
        given(orderRepository.findById(1)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.remove(1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order: 1");
    }

    @Test
    public void OrderService_UpdateStatus_SetsPaidAndDeductsStock() {
        // Arrange
        given(orderRepository.findByTxRef("tx")).willReturn(Optional.of(order));

        // Act
        orderService.updateStatus("success", "tx");

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(product.getStock()).isEqualTo(3);
        verify(productRepository, times(1)).saveAll(any());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void OrderService_UpdateStatus_Throws_WhenStockNegative() {
        // Arrange
        product.setStock(1);
        given(orderRepository.findByTxRef("tx")).willReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.updateStatus("success", "tx"))
                .isInstanceOf(OrderProcessException.class)
                .hasMessage("Product Stock Limited");
    }

    @Test
    public void OrderService_UpdateStatus_SetsCancelled() {
        // Arrange
        given(orderRepository.findByTxRef("tx")).willReturn(Optional.of(order));

        // Act
        orderService.updateStatus("failed/cancelled", "tx");

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository, times(1)).save(order);
    }
}
