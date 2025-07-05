package com.firomsa.ecommerce.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "rsa")
public class RSAConfig {
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
}
