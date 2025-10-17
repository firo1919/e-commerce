package com.firomsa.ecommerce.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.repository.RoleRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RolesLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final ObjectMapper objectMapper;

    public RolesLoader(RoleRepository roleRepository, ObjectMapper objectMapper) {
        this.roleRepository = roleRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            try {
                // Load the JSON file from resources
                var resource = getClass().getClassLoader().getResourceAsStream("data/roles.json");
                if (resource == null) {
                    log.warn("Could not find roles.json in resources.");
                    return;
                }
                java.util.List<java.util.Map<String, String>> rolesList = objectMapper.readValue(
                        resource,
                        objectMapper.getTypeFactory().constructCollectionType(java.util.List.class,
                                java.util.Map.class));
                for (var roleMap : rolesList) {
                    String roleName = roleMap.get("name");
                    var role = Role.builder()
                            .name(roleName)
                            .build();
                    roleRepository.save(role);
                }
                log.info("Roles loaded successfully from JSON.");
            } catch (Exception e) {
                log.error("Failed to load roles from JSON", e);
            }
        }
    }

}
