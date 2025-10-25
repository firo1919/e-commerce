package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.firomsa.ecommerce.exception.OrderProcessException;
import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.OrderRepository;
import com.yaphet.chapa.Chapa;
import com.yaphet.chapa.model.Customization;
import com.yaphet.chapa.model.InitializeResponseData;
import com.yaphet.chapa.model.PostData;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {

    @Mock
    private Chapa chapa;

    @Mock
    private Customization customization;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Order order;

    @BeforeEach
    void setup() {
        Address addr = Address.builder().id(1).firstName("F").lastName("A").active(true).build();
        User user = User.builder().email("e@example.com").addresses(List.of(addr)).createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
        order = Order.builder().user(user).totalPrice(100.0).createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
    }

    @Test
    public void PaymentService_StartTransaction_Succeeds() throws Throwable {
        // Arrange
        InitializeResponseData response = new InitializeResponseData();
        org.mockito.BDDMockito.given(chapa.initialize(any(PostData.class))).willReturn(response);

        // Act
        InitializeResponseData result = paymentService.startTransaction(order);

        // Assert
        assertThat(result).isSameAs(response);
        assertThat(order.getTxRef()).isNotBlank();
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void PaymentService_StartTransaction_Throws_OnFailure() throws Throwable {
        // Arrange
        org.mockito.BDDMockito.given(chapa.initialize(any(PostData.class))).willThrow(new RuntimeException("fail"));

        // Act & Assert
        assertThatThrownBy(() -> paymentService.startTransaction(order))
                .isInstanceOf(OrderProcessException.class)
                .hasMessage("Failed to initialize payment gateway");
    }
}
