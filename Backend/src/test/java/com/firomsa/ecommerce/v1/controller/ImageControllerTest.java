package com.firomsa.ecommerce.v1.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.UrlResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.firomsa.ecommerce.security.JWTSecurityFilter;
import com.firomsa.ecommerce.v1.dto.ImageDTO;
import com.firomsa.ecommerce.v1.service.ImageService;
import com.firomsa.ecommerce.v1.service.JWTAuthService;
import com.firomsa.ecommerce.v1.service.StorageService;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @MockitoBean
    private StorageService storageService;

    @MockitoBean
    private JWTAuthService jwtAuthService;

    @MockitoBean
    private JWTSecurityFilter jwtSecurityFilter;

    @MockitoBean
    private ImageService imageService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllImages_returnsList() throws Exception {
        given(imageService.getAll()).willReturn(java.util.List.of(ImageDTO.builder().name("a").build()));
        mockMvc.perform(get("/api/v1/images"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(imageService, times(1)).getAll();
    }

    @Test
    void getImage_returnsResource() throws Exception {
        UrlResource resource = new UrlResource(Path.of("/tmp").toUri());
        given(storageService.getImage("img")).willReturn(resource);
        mockMvc.perform(get("/api/v1/images/{id}", "img"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(storageService, times(1)).getImage("img");
    }

    @Test
    void deleteImage_noContent() throws Exception {
        mockMvc.perform(delete("/api/v1/images/{id}", "img"))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(imageService, times(1)).remove("img");
    }
}
