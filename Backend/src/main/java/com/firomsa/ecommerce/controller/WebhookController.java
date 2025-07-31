package com.firomsa.ecommerce.controller;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firomsa.ecommerce.config.PaymentConfig;
import com.firomsa.ecommerce.dto.ChapaResponse;
import com.firomsa.ecommerce.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/webhook")
@Tag(name = "Webhooks", description = "API for managing webhooks")
@Slf4j
public class WebhookController {

    private final PaymentConfig paymentConfig;
    private final OrderService orderService;

    public WebhookController(PaymentConfig paymentConfig, OrderService orderService) {
        this.paymentConfig = paymentConfig;
        this.orderService = orderService;
    }

    @Operation(summary = "Webhook event listener for chapa payment")
    @PostMapping("/payment")
    public ResponseEntity<Void> chapaWebhook(@RequestHeader HttpHeaders headers,
            @Valid @RequestBody String rawBody) {
        String signatureOne = headers.getFirst("x-chapa-signature");
        String signatureTwo = headers.getFirst("chapa-signature");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String computedHash = hmacSha256(rawBody, paymentConfig.getEncription());
            if (computedHash.equalsIgnoreCase(signatureOne) || computedHash.equalsIgnoreCase(signatureTwo)) {
                ChapaResponse response = objectMapper.readValue(rawBody, ChapaResponse.class);
                log.info("Valid webhook signature");
                orderService.updateStatus(response.getStatus(), response.getTx_ref());
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        } catch (Exception e) {
            log.error("Error verifying signature", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static String hmacSha256(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
}
