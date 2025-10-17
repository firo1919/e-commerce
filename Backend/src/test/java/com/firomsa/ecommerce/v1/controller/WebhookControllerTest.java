package com.firomsa.ecommerce.v1.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firomsa.ecommerce.config.PaymentConfig;
import com.firomsa.ecommerce.security.JWTSecurityFilter;
import com.firomsa.ecommerce.v1.dto.ChapaResponse;
import com.firomsa.ecommerce.v1.service.JWTAuthService;
import com.firomsa.ecommerce.v1.service.OrderService;

@WebMvcTest(WebhookController.class)
@AutoConfigureMockMvc(addFilters = false)
class WebhookControllerTest {

    @MockitoBean
    private PaymentConfig paymentConfig;

    @MockitoBean
    private JWTAuthService jwtAuthService;

    @MockitoBean
    private JWTSecurityFilter jwtSecurityFilter;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void webhook_validSignature_returnsNoContent() throws Exception {
        ChapaResponse response = new ChapaResponse();
        response.setStatus("success");
        response.setTx_ref("tx");
        String body = objectMapper.writeValueAsString(response);
        String secret = "secret";
        javax.crypto.Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(body.getBytes(StandardCharsets.UTF_8));
        String hex = java.util.HexFormat.of().formatHex(hash);
        org.mockito.BDDMockito.given(paymentConfig.getEncription()).willReturn(secret);

        mockMvc.perform(post("/api/v1/webhook/payment").header("x-chapa-signature", hex).content(body))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).updateStatus("success", "tx");
    }

    @Test
    void webhook_invalidSignature_returnsUnauthorized() throws Exception {
        ChapaResponse response = new ChapaResponse();
        response.setStatus("success");
        response.setTx_ref("tx");
        String body = objectMapper.writeValueAsString(response);
        org.mockito.BDDMockito.given(paymentConfig.getEncription()).willReturn("secret");

        mockMvc.perform(post("/api/v1/webhook/payment").header("x-chapa-signature", "bad").content(body))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
