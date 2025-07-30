package com.firomsa.ecommerce.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.firomsa.ecommerce.exception.OrderProcessException;
import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.repository.OrderRepository;
import com.yaphet.chapa.Chapa;
import com.yaphet.chapa.model.Customization;
import com.yaphet.chapa.model.InitializeResponseData;
import com.yaphet.chapa.model.PostData;
import com.yaphet.chapa.utility.Util;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentService {
    private final Chapa chapa;
    private final Customization customization;
    private final OrderRepository orderRepository;

    public PaymentService(Chapa chapa, Customization customization, OrderRepository orderRepository) {
        this.chapa = chapa;
        this.customization = customization;
        this.orderRepository = orderRepository;
    }

    public InitializeResponseData startTransaction(Order order) {
        String txRef = Util.generateToken();
        PostData postData = new PostData()
                .setAmount(BigDecimal.valueOf(order.getTotalPrice()))
                .setCurrency("ETB")
                .setFirstName(order.getUser().getFirstName())
                .setLastName(order.getUser().getLastName())
                .setEmail(order.getUser().getEmail())
                .setTxRef(txRef)
                .setCallbackUrl("https://chapa.co")
                .setReturnUrl("https://chapa.co")
                .setSubAccountId("testSubAccountId")
                .setCustomization(customization);
        InitializeResponseData responseData;
        try {
            responseData = chapa.initialize(postData);
        } catch (Throwable e) {
            log.info(e.getMessage());
            throw new OrderProcessException("Failed to initialize payment gateway");
        }
        order.setTxRef(txRef);
        orderRepository.save(order);
        
        return responseData;
    }
}
