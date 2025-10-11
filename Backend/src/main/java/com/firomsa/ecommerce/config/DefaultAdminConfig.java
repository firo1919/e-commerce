package com.firomsa.ecommerce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "admin")
public class DefaultAdminConfig {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
