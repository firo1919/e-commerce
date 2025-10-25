package com.firomsa.ecommerce.v1.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.firomsa.ecommerce.security.JWTSecurityFilter;
import com.firomsa.ecommerce.v1.dto.AddressResponseDTO;
import com.firomsa.ecommerce.v1.service.AddressService;
import com.firomsa.ecommerce.v1.service.JWTAuthService;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
class AddressControllerTest {

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private JWTAuthService jwtAuthService;

    @MockitoBean
    private JWTSecurityFilter jwtSecurityFilter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllAddresses_returnsList() throws Exception {
        given(addressService.getAll()).willReturn(List.of(AddressResponseDTO.builder().id(1).street("s").build()));
        mockMvc.perform(get("/api/v1/addresses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(1)));
        verify(addressService, times(1)).getAll();
    }

    @Test
    void getAddress_returnsOne() throws Exception {
        given(addressService.get(1)).willReturn(AddressResponseDTO.builder().id(1).street("s").build());
        mockMvc.perform(get("/api/v1/addresses/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(1)));
        verify(addressService, times(1)).get(1);
    }
}
