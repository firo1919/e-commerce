package com.firomsa.ecommerce.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.RoleRepository;
import com.firomsa.ecommerce.repository.UserRepository;

@Component
public class AdminSetup implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultAdminConfig adminProperties;

    public AdminSetup(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, DefaultAdminConfig adminProperties) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail(adminProperties.getEmail())
                && !userRepository.existsByUsername(adminProperties.getUsername())) {
            LocalDateTime now = LocalDateTime.now();
            Role role = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));
            User admin = User.builder()
                    .username(adminProperties.getUsername())
                    .email(adminProperties.getEmail())
                    .password(passwordEncoder.encode(adminProperties.getPassword()))
                    .firstName(adminProperties.getFirstName())
                    .lastName(adminProperties.getLastName())
                    .role(role)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            userRepository.save(admin);
        }
    }

}
