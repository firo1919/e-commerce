package com.firomsa.ecommerce.v1.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.firomsa.ecommerce.security.JWTSecurityFilter;
import com.firomsa.ecommerce.v1.dto.ImageDTO;
import com.firomsa.ecommerce.v1.dto.ProductRequestDTO;
import com.firomsa.ecommerce.v1.dto.ProductResponseDTO;
import com.firomsa.ecommerce.v1.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.v1.service.JWTAuthService;
import com.firomsa.ecommerce.v1.service.ProductService;
import com.firomsa.ecommerce.v1.service.StorageService;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JWTAuthService jwtAuthService;

    @MockitoBean
    private JWTSecurityFilter jwtSecurityFilter;

    @MockitoBean
    private StorageService storageService;

    @Autowired
    private MockMvc mockMvc;

    private UUID productId;
    private ProductRequestDTO req;
    private ProductResponseDTO res;

    @BeforeEach
    void setup() {
        productId = UUID.randomUUID();
        req = ProductRequestDTO.builder().name("Phone").description("Smart").price(10.0).stock(5).categories(List.of())
                .build();
        res = ProductResponseDTO.builder().id(productId.toString()).name("Phone").price(10.0).build();
    }

    @Test
    void getAllProducts_returnsList() throws Exception {
        given(productService.getAll()).willReturn(List.of(res));
        mockMvc.perform(get("/api/v1/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(1)));
        verify(productService, times(1)).getAll();
    }

    @Test
    void getProduct_returnsOne() throws Exception {
        given(productService.get(productId)).willReturn(res);
        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(res.getId())));
        verify(productService, times(1)).get(productId);
    }

    @Test
    void addProduct_createsWithLocation() throws Exception {
        given(productService.create(org.mockito.ArgumentMatchers.any(ProductRequestDTO.class))).willReturn(res);
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", CoreMatchers.is(res.getId())));
        verify(productService, times(1)).create(org.mockito.ArgumentMatchers.any(ProductRequestDTO.class));
    }

    @Test
    void updateProduct_updates() throws Exception {
        given(productService.update(org.mockito.ArgumentMatchers.any(ProductRequestDTO.class),
                org.mockito.ArgumentMatchers.eq(productId))).willReturn(res);
        mockMvc.perform(put("/api/v1/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(res.getId())));
        verify(productService, times(1)).update(org.mockito.ArgumentMatchers.any(ProductRequestDTO.class),
                org.mockito.ArgumentMatchers.eq(productId));
    }

    @Test
    void deleteProduct_forceTrue_callsRemove() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", productId).param("force", "true"))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(productService, times(1)).remove(productId);
    }

    @Test
    void deleteProduct_forceFalse_callsSoftDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", productId).param("force", "false"))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(productService, times(1)).softDelete(productId);
    }

    @Test
    void deleteProduct_noParam_callsSoftDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", productId))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(productService, times(1)).softDelete(productId);
    }

    @Test
    void getAllProductImages_returnsList() throws Exception {
        given(storageService.getProductImages(productId)).willReturn(List.of(ImageDTO.builder().name("a").build()));
        mockMvc.perform(get("/api/v1/products/{id}/productImages", productId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(1)));
        verify(storageService, times(1)).getProductImages(productId);
    }

    @Test
    void uploadProductImage_creates() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", new byte[] { 1, 2, 3 });
        given(storageService.addProductImage(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.eq(productId)))
                .willReturn(ImageDTO.builder().name("a").build());
        mockMvc.perform(multipart("/api/v1/products/{id}/productImages", productId).file(file))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(storageService, times(1)).addProductImage(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.eq(productId));
    }

    @Test
    void getProductReviews_returnsList() throws Exception {
        given(productService.getReviews(productId)).willReturn(List.of(ReviewResponseDTO.builder().id(1).build()));
        mockMvc.perform(get("/api/v1/products/{id}/reviews", productId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(1)));
        verify(productService, times(1)).getReviews(productId);
    }
}
