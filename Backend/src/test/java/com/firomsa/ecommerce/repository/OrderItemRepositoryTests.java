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

import com.firomsa.ecommerce.model.Category;
import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.model.OrderItem;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

@DataJpaTest
@ActiveProfiles("test")
public class OrderItemRepositoryTests {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private Role role;
    private Category testCategory;
    private User testUser;
    private Product testProduct;
    private Order testOrder;
    private OrderItem testOrderItem1;
    private OrderItem testOrderItem2;

    @BeforeEach
    void setup() {
        role = roleRepository.save(Role.builder().name("USER").build());
        testCategory = categoryRepository.save(Category.builder().name("Electronics").build());

        testUser = userRepository.save(User.builder()
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        testProduct = Product.builder()
                .name("Test Product")
                .description("A test product")
                .price(100.0)
                .stock(10)
                .categories(List.of(testCategory))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testOrder = Order.builder()
                .user(testUser)
                .totalPrice(200.0)
                .txRef("TXN123456")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testOrderItem1 = OrderItem.builder()
                .priceAtPurchase(100.0)
                .quantity(2)
                .product(testProduct)
                .order(testOrder)
                .build();

        testOrderItem2 = OrderItem.builder()
                .priceAtPurchase(50.0)
                .quantity(1)
                .product(testProduct)
                .order(testOrder)
                .build();
    }

    @Test
    public void OrderItemRepository_Save_ReturnSavedOrderItem() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);
        Order savedOrder = orderRepository.save(testOrder);

        OrderItem orderItem = testOrderItem1;
        orderItem.setProduct(savedProduct);
        orderItem.setOrder(savedOrder);

        // Act
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // Assert
        assertThat(savedOrderItem).isNotNull();
        assertThat(savedOrderItem.getId()).isNotNull();
        assertThat(savedOrderItem).usingRecursiveComparison().isEqualTo(orderItem);
    }

    @Test
    public void OrderItemRepository_FindAll_ReturnMoreThanOneOrderItem() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);
        Order savedOrder = orderRepository.save(testOrder);

        OrderItem orderItem1 = testOrderItem1;
        OrderItem orderItem2 = testOrderItem2;
        orderItem1.setProduct(savedProduct);
        orderItem1.setOrder(savedOrder);
        orderItem2.setProduct(savedProduct);
        orderItem2.setOrder(savedOrder);

        // Act
        orderItemRepository.save(orderItem1);
        orderItemRepository.save(orderItem2);
        List<OrderItem> savedOrderItems = orderItemRepository.findAll();

        // Assert
        assertThat(savedOrderItems).isNotNull();
        assertThat(savedOrderItems.size()).isEqualTo(2);
        assertThat(savedOrderItems).extracting(OrderItem::getQuantity).containsExactlyInAnyOrder(2, 1);
        assertThat(savedOrderItems).extracting(OrderItem::getPriceAtPurchase).containsExactlyInAnyOrder(100.0, 50.0);
    }

    @Test
    public void OrderItemRepository_FindById_ReturnOrderItem() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);
        Order savedOrder = orderRepository.save(testOrder);

        OrderItem orderItem = testOrderItem1;
        orderItem.setProduct(savedProduct);
        orderItem.setOrder(savedOrder);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // Act
        Optional<OrderItem> foundOrderItem = orderItemRepository.findById(savedOrderItem.getId());

        // Assert
        assertThat(foundOrderItem).isPresent();
        OrderItem retrievedOrderItem = foundOrderItem.get();
        assertThat(retrievedOrderItem).isNotNull();
        assertThat(retrievedOrderItem).usingRecursiveComparison().isEqualTo(savedOrderItem);
    }

    @Test
    public void OrderItemRepository_FindById_ReturnEmpty() {
        // Arrange
        Integer nonExistentId = 999;

        // Act
        Optional<OrderItem> foundOrderItem = orderItemRepository.findById(nonExistentId);

        // Assert
        assertThat(foundOrderItem).isEmpty();
    }

    @Test
    public void OrderItemRepository_DeleteById_DeleteOrderItem() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);
        Order savedOrder = orderRepository.save(testOrder);

        OrderItem orderItem = testOrderItem1;
        orderItem.setProduct(savedProduct);
        orderItem.setOrder(savedOrder);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // Act
        orderItemRepository.deleteById(savedOrderItem.getId());

        // Assert
        assertThat(orderItemRepository.existsById(savedOrderItem.getId())).isFalse();
    }

    @Test
    public void OrderItemRepository_Delete_DeleteOrderItem() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);
        Order savedOrder = orderRepository.save(testOrder);

        OrderItem orderItem = testOrderItem1;
        orderItem.setProduct(savedProduct);
        orderItem.setOrder(savedOrder);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // Act
        orderItemRepository.delete(savedOrderItem);

        // Assert
        assertThat(orderItemRepository.existsById(savedOrderItem.getId())).isFalse();
    }
}
