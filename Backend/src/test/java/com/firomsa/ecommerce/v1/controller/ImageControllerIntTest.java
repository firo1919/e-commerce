package com.firomsa.ecommerce.v1.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.firomsa.ecommerce.model.Image;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.repository.CategoryRepository;
import com.firomsa.ecommerce.repository.ImageRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.v1.service.StorageService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class ImageControllerIntTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ImageRepository imageRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @MockitoBean
        private StorageService storageService;

        private Product testProduct;
        private Image testImage;

        @BeforeEach
        void setUp() {

                testProduct = Product.builder()
                                .name("Test Product")
                                .description("Test Product Description")
                                .price(99.99)
                                .stock(10)
                                .active(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                testImage = Image.builder()
                                .name("test-image.jpg")
                                .build();
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ImageController_GetAllImages_ReturnsListOfImages() throws Exception {
                // Arrange

                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);
                testImage.setProduct(savedProduct);
                List<Image> images = imageRepository.saveAll(List.of(testImage));

                // Act and Assert
                mockMvc.perform(get("/api/v1/images").contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()", CoreMatchers.is(images.size())))
                                .andExpect(jsonPath("$[0].name", CoreMatchers.is(images.getFirst().getName())));
        }

        @Test
        void ImageController_GetAllImages_Returns401_WhenNotAuthenticated() throws Exception {
                // Act and Assert
                mockMvc.perform(get("/api/v1/images").contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ImageController_GetImage_ReturnsResource_WhenImageExists() throws Exception {
                // Arrange
                UrlResource resource = new UrlResource(Path.of("/tmp").toUri());
                given(storageService.getImage("img")).willReturn(resource);

                // Act and Assert
                mockMvc.perform(get("/api/v1/images/{id}", "img"))
                                .andDo(print())
                                .andExpect(status().isOk());

        }

        // @Test
        // @WithMockUser(username = "admin", roles = { "ADMIN" })
        // void ImageController_GetImage_Returns404_WhenImageNotFound() throws Exception
        // {
        // // Arrange
        // String nonExistentId = "non-existent-id";

        // // Act and Assert
        // mockMvc.perform(get("/api/v1/images/{id}", nonExistentId)
        // .contentType(MediaType.APPLICATION_JSON))
        // .andExpect(status().isNotFound());
        // }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ImageController_DeleteImage_Returns204_WhenImageExists() throws Exception {
                // Arrange
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);
                testImage.setProduct(savedProduct);
                Image savedImage = imageRepository.save(testImage);

                // Act and Assert
                mockMvc.perform(delete("/api/v1/images/{id}", savedImage.getName())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ImageController_DeleteImage_Returns404_WhenImageNotFound() throws Exception {
                // Arrange
                String nonExistentId = "non-existent-id";

                // Act and Assert
                mockMvc.perform(delete("/api/v1/images/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }
}
