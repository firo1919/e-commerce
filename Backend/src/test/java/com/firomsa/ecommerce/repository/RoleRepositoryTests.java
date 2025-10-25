package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.Role;

@DataJpaTest
@ActiveProfiles("test")
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    private final Role userRole = Role.builder()
            .name("USER")
            .build();

    private final Role adminRole = Role.builder()
            .name("ADMIN")
            .build();

    @Test
    public void RoleRepository_Save_ReturnSavedRole() {
        // Arrange
        Role role = userRole;

        // Act
        Role savedRole = roleRepository.save(role);

        // Assert
        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole).usingRecursiveComparison().isEqualTo(userRole);
    }

    @Test
    public void RoleRepository_FindAll_ReturnMoreThanOneRole() {
        // Arrange
        Role role1 = userRole;
        Role role2 = adminRole;

        // Act
        roleRepository.save(role1);
        roleRepository.save(role2);
        List<Role> savedRoles = roleRepository.findAll();

        // Assert
        assertThat(savedRoles).isNotNull();
        assertThat(savedRoles.size()).isEqualTo(2);
        assertThat(savedRoles).extracting(Role::getName).containsExactlyInAnyOrder("USER", "ADMIN");
    }

    @Test
    public void RoleRepository_FindById_ReturnRole() {
        // Arrange
        Role role = userRole;

        // Act
        Role savedRole = roleRepository.save(role);
        Optional<Role> foundRole = roleRepository.findById(savedRole.getId());

        // Assert
        assertThat(foundRole).isPresent();
        Role retrievedRole = foundRole.get();
        assertThat(retrievedRole).isNotNull();
        assertThat(retrievedRole).usingRecursiveComparison().isEqualTo(savedRole);
    }

    @Test
    public void RoleRepository_FindById_ReturnEmpty() {
        // Arrange
        Integer nonExistentId = 999;

        // Act
        Optional<Role> foundRole = roleRepository.findById(nonExistentId);

        // Assert
        assertThat(foundRole).isEmpty();
    }

    @Test
    public void RoleRepository_FindByName_ReturnRole() {
        // Arrange
        Role role = userRole;

        // Act
        roleRepository.save(role);
        Optional<Role> foundRole = roleRepository.findByName("USER");

        // Assert
        assertThat(foundRole).isPresent();
        Role retrievedRole = foundRole.get();
        assertThat(retrievedRole).isNotNull();
        assertThat(retrievedRole).usingRecursiveComparison().isEqualTo(role);
        assertThat(retrievedRole.getName()).isEqualTo("USER");
    }

    @Test
    public void RoleRepository_FindByName_ReturnEmpty() {
        // Arrange
        Role role = userRole;

        // Act
        roleRepository.save(role);
        Optional<Role> foundRole = roleRepository.findByName("NONEXISTENT");

        // Assert
        assertThat(foundRole).isEmpty();
    }

    @Test
    public void RoleRepository_DeleteById_DeleteRole() {
        // Arrange
        Role role = userRole;
        Role savedRole = roleRepository.save(role);

        // Act
        roleRepository.deleteById(savedRole.getId());

        // Assert
        assertThat(roleRepository.existsById(savedRole.getId())).isFalse();
    }

    @Test
    public void RoleRepository_Delete_DeleteRole() {
        // Arrange
        Role role = userRole;
        Role savedRole = roleRepository.save(role);

        // Act
        roleRepository.delete(savedRole);

        // Assert
        assertThat(roleRepository.existsById(savedRole.getId())).isFalse();
    }
}
