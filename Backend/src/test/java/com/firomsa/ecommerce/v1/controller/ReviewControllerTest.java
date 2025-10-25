package com.firomsa.ecommerce.v1.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.firomsa.ecommerce.security.JWTSecurityFilter;
import com.firomsa.ecommerce.v1.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.v1.service.JWTAuthService;
import com.firomsa.ecommerce.v1.service.ReviewService;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JWTAuthService jwtAuthService;

    @MockitoBean
    private JWTSecurityFilter jwtSecurityFilter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllReviews_returnsList() throws Exception {
        given(reviewService.getAll()).willReturn(java.util.List.of(ReviewResponseDTO.builder().id(1).build()));
        mockMvc.perform(get("/api/v1/reviews"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(1)));
        verify(reviewService, times(1)).getAll();
    }

    @Test
    void getReview_returnsOne() throws Exception {
        given(reviewService.get(1)).willReturn(ReviewResponseDTO.builder().id(1).build());
        mockMvc.perform(get("/api/v1/reviews/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(1)));
        verify(reviewService, times(1)).get(1);
    }

    @Test
    void deleteReview_noContent() throws Exception {
        mockMvc.perform(delete("/api/v1/reviews/{id}", 1))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(reviewService, times(1)).remove(1);
    }
}
