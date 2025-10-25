package com.firomsa.ecommerce.v1.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.AddressRepository;
import com.firomsa.ecommerce.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class AddressControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private Role role;
    private User testUser;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        role = Role.builder().name("USER").id(1).build();
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password123")
                .role(role)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();

        testAddress = Address.builder()
                .firstName("John")
                .lastName("Doe")
                .street("123 Main St")
                .city("New York")
                .state("NY")
                .zipCode("10001")
                .country("USA")
                .phone("1234567890")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void AddressController_GetAllAddresses_ReturnsListOfAddresses() throws Exception {
        // Arrange
        User savedUser = userRepository.save(testUser);
        testAddress.setUser(savedUser);
        List<Address> addresses = addressRepository.saveAll(List.of(testAddress));

        // Act and Assert
        mockMvc.perform(get("/api/v1/addresses").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(addresses.size())))
                .andExpect(jsonPath("$[0].firstName", CoreMatchers.is(addresses.getFirst().getFirstName())))
                .andExpect(jsonPath("$[0].lastName", CoreMatchers.is(addresses.getFirst().getLastName())))
                .andExpect(jsonPath("$[0].street", CoreMatchers.is(addresses.getFirst().getStreet())))
                .andExpect(jsonPath("$[0].city", CoreMatchers.is(addresses.getFirst().getCity())))
                .andExpect(jsonPath("$[0].state", CoreMatchers.is(addresses.getFirst().getState())))
                .andExpect(jsonPath("$[0].zipCode", CoreMatchers.is(addresses.getFirst().getZipCode())))
                .andExpect(jsonPath("$[0].country", CoreMatchers.is(addresses.getFirst().getCountry())))
                .andExpect(jsonPath("$[0].phone", CoreMatchers.is(addresses.getFirst().getPhone())))
                .andExpect(jsonPath("$[0].active", CoreMatchers.is(addresses.getFirst().getActive())));
    }

    @Test
    void AddressController_GetAllAddresses_Returns401_WhenNotAuthenticated() throws Exception {
        // Act and Assert
        mockMvc.perform(get("/api/v1/addresses").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void AddressController_GetAddress_ReturnsAddress_WhenAddressExists() throws Exception {
        // Arrange
        User savedUser = userRepository.save(testUser);
        testAddress.setUser(savedUser);
        Address savedAddress = addressRepository.save(testAddress);

        // Act and Assert
        mockMvc.perform(get("/api/v1/addresses/{id}", savedAddress.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", CoreMatchers.is(savedAddress.getFirstName())))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is(savedAddress.getLastName())))
                .andExpect(jsonPath("$.street", CoreMatchers.is(savedAddress.getStreet())))
                .andExpect(jsonPath("$.city", CoreMatchers.is(savedAddress.getCity())))
                .andExpect(jsonPath("$.state", CoreMatchers.is(savedAddress.getState())))
                .andExpect(jsonPath("$.zipCode", CoreMatchers.is(savedAddress.getZipCode())))
                .andExpect(jsonPath("$.country", CoreMatchers.is(savedAddress.getCountry())))
                .andExpect(jsonPath("$.phone", CoreMatchers.is(savedAddress.getPhone())))
                .andExpect(jsonPath("$.active", CoreMatchers.is(savedAddress.getActive())));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void AddressController_GetAddress_Returns404_WhenAddressNotFound() throws Exception {
        // Arrange
        int nonExistentId = 999;

        // Act and Assert
        mockMvc.perform(get("/api/v1/addresses/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
