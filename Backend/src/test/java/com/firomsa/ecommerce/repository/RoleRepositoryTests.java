package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.firomsa.ecommerce.model.Role;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void RoleRepository_FindByName_ReturnRole() {

        Role user = roleRepository.save(Role.builder()
        .name("USER")
        .build());

        Role savedRole = roleRepository.findByName("USER").get();

        assertThat(savedRole.getName()).isEqualTo(user.getName());
    }
}
