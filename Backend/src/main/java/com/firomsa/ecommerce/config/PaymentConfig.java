package com.firomsa.ecommerce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.yaphet.chapa.Chapa;
import com.yaphet.chapa.model.Customization;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "chapa")
public class PaymentConfig {
    private String secret;
    private String encription;

    @Bean
    public Chapa chapa() {
        return new Chapa(secret);
    }

    @Bean
    public Customization customization() {
        return new Customization()
                .setTitle("E-commerce")
                .setDescription("It is time to pay");
    }
}
