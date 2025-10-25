package com.firomsa.ecommerce.v1.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.firomsa.ecommerce.v1.dto.CartResponseDTO;
import com.firomsa.ecommerce.v1.service.CartService;
import com.firomsa.ecommerce.v1.service.JWTAuthService;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JWTAuthService jwtAuthService;

    @MockitoBean
    private JWTSecurityFilter jwtSecurityFilter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllCarts_returnsList() throws Exception {
        given(cartService.getAll()).willReturn(List.of(CartResponseDTO.builder().id(1).quantity(2).build()));
        mockMvc.perform(get("/api/v1/carts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(1)));
        verify(cartService, times(1)).getAll();
    }

    @Test
    void getCart_returnsOne() throws Exception {
        given(cartService.get(1)).willReturn(CartResponseDTO.builder().id(1).quantity(2).build());
        mockMvc.perform(get("/api/v1/carts/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(1)));
        verify(cartService, times(1)).get(1);
    }

    @Test
    void deleteCart_noContent() throws Exception {
        mockMvc.perform(delete("/api/v1/carts/{id}", 1))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(cartService, times(1)).remove(1);
    }
}
