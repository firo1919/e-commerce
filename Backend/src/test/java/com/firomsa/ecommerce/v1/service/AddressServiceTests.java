package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.AddressRepository;
import com.firomsa.ecommerce.v1.dto.AddressResponseDTO;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AddressServiceTests {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private Address address;
    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(UUID.randomUUID())
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        address = Address.builder()
                .id(1)
                .firstName("Firomsa")
                .lastName("Assefa")
                .street("123 Main St")
                .city("Addis Ababa")
                .state("Addis Ababa")
                .zipCode("1000")
                .country("Ethiopia")
                .phone("+251911234567")
                .active(true)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void AddressService_GetAll_ReturnsAddresses() {
        // Arrange
        given(addressRepository.findAll()).willReturn(List.of(address));

        // Act
        List<AddressResponseDTO> result = addressService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    public void AddressService_Get_ReturnsAddress() {
        // Arrange
        given(addressRepository.findById(1)).willReturn(Optional.of(address));

        // Act
        AddressResponseDTO result = addressService.get(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(address.getId());
        verify(addressRepository, times(1)).findById(1);
    }

    @Test
    public void AddressService_Get_Throws_WhenNotFound() {
        // Arrange
        given(addressRepository.findById(1)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> addressService.get(1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Address: 1");
    }
}
