package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

@DataJpaTest
@ActiveProfiles("test")
public class AddressRepositoryTests {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role role;

    private User testUser;

    @BeforeEach
    void setup() {
        role = roleRepository.save(Role.builder().name("USER").build());

        testUser = User.builder()
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private final Address testAddress1 = Address.builder()
            .firstName("Firomsa")
            .lastName("Assefa")
            .street("123 Main St")
            .city("Addis Ababa")
            .state("Addis Ababa")
            .zipCode("1000")
            .country("Ethiopia")
            .phone("+251911234567")
            .active(true)
            .user(testUser)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    private final Address testAddress2 = Address.builder()
            .firstName("Firomsa")
            .lastName("Assefa")
            .street("456 Secondary St")
            .city("Addis Ababa")
            .state("Addis Ababa")
            .zipCode("1001")
            .country("Ethiopia")
            .phone("+251911234568")
            .active(false)
            .user(testUser)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    @Test
    public void AddressRepository_Save_ReturnSavedAddress() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Address address = testAddress1;
        address.setUser(savedUser);

        // Act
        Address savedAddress = addressRepository.save(address);

        // Assert
        assertThat(savedAddress).isNotNull();
        assertThat(savedAddress.getId()).isNotNull();
        assertThat(savedAddress).usingRecursiveComparison().isEqualTo(address);
        assertThat(savedAddress.getCreatedAt()).isNotNull();
        assertThat(savedAddress.getUpdatedAt()).isNotNull();
    }

    @Test
    public void AddressRepository_FindById_ReturnAddress() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Address address = testAddress1;
        address.setUser(savedUser);
        Address savedAddress = addressRepository.save(address);

        // Act
        Optional<Address> foundAddress = addressRepository.findById(savedAddress.getId());

        // Assert
        assertThat(foundAddress).isPresent();
        Address retrievedAddress = foundAddress.get();
        assertThat(retrievedAddress).isNotNull();
        assertThat(retrievedAddress).usingRecursiveComparison().isEqualTo(savedAddress);
    }

    @Test
    public void AddressRepository_FindById_ReturnEmpty() {
        // Arrange
        Integer nonExistentId = 999;

        // Act
        Optional<Address> foundAddress = addressRepository.findById(nonExistentId);

        // Assert
        assertThat(foundAddress).isEmpty();
    }

    @Test
    public void AddressRepository_FindByUserAndActive_ReturnActiveAddress() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Address address = testAddress1;
        address.setUser(savedUser);
        addressRepository.save(address);

        // Act
        Optional<Address> foundAddress = addressRepository.findByUserAndActive(savedUser, true);

        // Assert
        assertThat(foundAddress).isPresent();
        Address retrievedAddress = foundAddress.get();
        assertThat(retrievedAddress).isNotNull();
        assertThat(retrievedAddress).usingRecursiveComparison().isEqualTo(address);
        assertThat(retrievedAddress.getActive()).isTrue();
    }

    @Test
    public void AddressRepository_FindByUserAndActive_ReturnInactiveAddress() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Address address = testAddress2;
        address.setUser(savedUser);
        addressRepository.save(address);

        // Act
        Optional<Address> foundAddress = addressRepository.findByUserAndActive(savedUser, false);

        // Assert
        assertThat(foundAddress).isPresent();
        Address retrievedAddress = foundAddress.get();
        assertThat(retrievedAddress).isNotNull();
        assertThat(retrievedAddress).usingRecursiveComparison().isEqualTo(address);
        assertThat(retrievedAddress.getActive()).isFalse();
    }

    @Test
    public void AddressRepository_FindByUserAndActive_ReturnEmpty() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Address address = testAddress1;
        address.setUser(savedUser);
        addressRepository.save(address);

        // Act
        Optional<Address> foundAddress = addressRepository.findByUserAndActive(savedUser, false);

        // Assert
        assertThat(foundAddress).isEmpty();
    }

    @Test
    public void AddressRepository_DeleteById_DeleteAddress() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Address address = testAddress1;
        address.setUser(savedUser);
        Address savedAddress = addressRepository.save(address);

        // Act
        addressRepository.deleteById(savedAddress.getId());

        // Assert
        assertThat(addressRepository.existsById(savedAddress.getId())).isFalse();
    }

    @Test
    public void AddressRepository_Delete_DeleteAddress() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Address address = testAddress1;
        address.setUser(savedUser);
        Address savedAddress = addressRepository.save(address);

        // Act
        addressRepository.delete(savedAddress);

        // Assert
        assertThat(addressRepository.existsById(savedAddress.getId())).isFalse();
    }
}
