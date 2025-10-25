package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTests {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role role;
    private User testUser;
    private Order testOrder1;
    private Order testOrder2;

    @BeforeEach
    void setup() {
        role = roleRepository.save(Role.builder().name("USER").build());

        testUser = User.builder()
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role) // role is now initialized
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testOrder1 = Order.builder()
                .user(testUser)
                .totalPrice(200.0)
                .txRef("TXN123456")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testOrder2 = Order.builder()
                .user(testUser)
                .totalPrice(150.0)
                .txRef("TXN789012")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void OrderRepository_Save_ReturnSavedOrder() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Order order = testOrder1;
        order.setUser(savedUser);

        // Act
        Order savedOrder = orderRepository.save(order);

        // Assert
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder).usingRecursiveComparison().isEqualTo(order);
        assertThat(savedOrder.getCreatedAt()).isNotNull();
        assertThat(savedOrder.getUpdatedAt()).isNotNull();
    }

    @Test
    public void OrderRepository_FindAll_ReturnMoreThanOneOrder() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Order order1 = testOrder1;
        Order order2 = testOrder2;
        order1.setUser(savedUser);
        order2.setUser(savedUser);

        // Act
        orderRepository.save(order1);
        orderRepository.save(order2);
        List<Order> savedOrders = orderRepository.findAll();

        // Assert
        assertThat(savedOrders).isNotNull();
        assertThat(savedOrders.size()).isEqualTo(2);
        assertThat(savedOrders).extracting(Order::getTxRef).containsExactlyInAnyOrder("TXN123456", "TXN789012");
        assertThat(savedOrders).extracting(Order::getTotalPrice).containsExactlyInAnyOrder(200.0, 150.0);
    }

    @Test
    public void OrderRepository_FindById_ReturnOrder() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Order order = testOrder1;
        order.setUser(savedUser);
        Order savedOrder = orderRepository.save(order);

        // Act
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // Assert
        assertThat(foundOrder).isPresent();
        Order retrievedOrder = foundOrder.get();
        assertThat(retrievedOrder).isNotNull();
        assertThat(retrievedOrder).usingRecursiveComparison().isEqualTo(savedOrder);
    }

    @Test
    public void OrderRepository_FindById_ReturnEmpty() {
        // Arrange
        Integer nonExistentId = 999;

        // Act
        Optional<Order> foundOrder = orderRepository.findById(nonExistentId);

        // Assert
        assertThat(foundOrder).isEmpty();
    }

    @Test
    public void OrderRepository_FindByTxRef_ReturnOrder() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Order order = testOrder1;
        order.setUser(savedUser);
        orderRepository.save(order);

        // Act
        Optional<Order> foundOrder = orderRepository.findByTxRef("TXN123456");

        // Assert
        assertThat(foundOrder).isPresent();
        Order retrievedOrder = foundOrder.get();
        assertThat(retrievedOrder).isNotNull();
        assertThat(retrievedOrder).usingRecursiveComparison().isEqualTo(order);
        assertThat(retrievedOrder.getTxRef()).isEqualTo("TXN123456");
    }

    @Test
    public void OrderRepository_FindByTxRef_ReturnEmpty() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Order order = testOrder1;
        order.setUser(savedUser);
        orderRepository.save(order);

        // Act
        Optional<Order> foundOrder = orderRepository.findByTxRef("NONEXISTENT");

        // Assert
        assertThat(foundOrder).isEmpty();
    }

    @Test
    public void OrderRepository_DeleteById_DeleteOrder() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Order order = testOrder1;
        order.setUser(savedUser);
        Order savedOrder = orderRepository.save(order);

        // Act
        orderRepository.deleteById(savedOrder.getId());

        // Assert
        assertThat(orderRepository.existsById(savedOrder.getId())).isFalse();
    }

    @Test
    public void OrderRepository_Delete_DeleteOrder() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Order order = testOrder1;
        order.setUser(savedUser);
        Order savedOrder = orderRepository.save(order);

        // Act
        orderRepository.delete(savedOrder);

        // Assert
        assertThat(orderRepository.existsById(savedOrder.getId())).isFalse();
    }
}
