package com.firomsa.ecommerce.v1.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.repository.CategoryRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.v1.dto.ImageDTO;
import com.firomsa.ecommerce.v1.dto.ProductRequestDTO;
import com.firomsa.ecommerce.v1.service.StorageService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @MockitoBean
        private StorageService storageService;

        @Autowired
        private ObjectMapper objectMapper;

        private Product testProduct;

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
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_GetAllProducts_ReturnsListOfProducts() throws Exception {
                // Arrange
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                List<Product> products = productRepository.saveAll(List.of(testProduct));

                // Act and Assert
                mockMvc.perform(get("/api/v1/products").contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()", CoreMatchers.is(products.size())))
                                .andExpect(jsonPath("$[0].name", CoreMatchers.is(products.getFirst().getName())))
                                .andExpect(jsonPath("$[0].description",
                                                CoreMatchers.is(products.getFirst().getDescription())))
                                .andExpect(jsonPath("$[0].price", CoreMatchers.is(products.getFirst().getPrice())))
                                .andExpect(jsonPath("$[0].stock", CoreMatchers.is(products.getFirst().getStock())))
                                .andExpect(jsonPath("$[0].active", CoreMatchers.is(products.getFirst().isActive())));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_GetProduct_ReturnsProduct_WhenProductExists() throws Exception {
                // Arrange
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);

                // Act and Assert
                mockMvc.perform(get("/api/v1/products/{id}", savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", CoreMatchers.is(savedProduct.getName())))
                                .andExpect(jsonPath("$.description", CoreMatchers.is(savedProduct.getDescription())))
                                .andExpect(jsonPath("$.price", CoreMatchers.is(savedProduct.getPrice())))
                                .andExpect(jsonPath("$.stock", CoreMatchers.is(savedProduct.getStock())))
                                .andExpect(jsonPath("$.active", CoreMatchers.is(savedProduct.isActive())));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_GetProduct_Returns404_WhenProductNotFound() throws Exception {
                // Arrange
                UUID nonExistentId = UUID.randomUUID();

                // Act and Assert
                mockMvc.perform(get("/api/v1/products/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_AddProduct_ReturnsProduct_WhenValidData() throws Exception {
                // Arrange
                ProductRequestDTO productRequest = ProductRequestDTO.builder()
                                .name("New Product")
                                .description("New Product Description")
                                .price(149.99)
                                .stock(5)
                                .categories(List.of())
                                .build();

                // Act and Assert
                mockMvc.perform(post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productRequest)))
                                .andDo(print()).andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name", CoreMatchers.is(productRequest.getName())))
                                .andExpect(jsonPath("$.description", CoreMatchers.is(productRequest.getDescription())))
                                .andExpect(jsonPath("$.price", CoreMatchers.is(productRequest.getPrice())))
                                .andExpect(jsonPath("$.stock", CoreMatchers.is(productRequest.getStock())));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_AddProduct_Returns400_WhenInvalidData() throws Exception {
                // Arrange
                ProductRequestDTO invalidRequest = ProductRequestDTO.builder()
                                .name("") // Invalid: empty name
                                .description("Valid Description")
                                .price(-10.0) // Invalid: negative price
                                .stock(-1) // Invalid: negative stock
                                .build();

                // Act and Assert
                mockMvc.perform(post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_UpdateProduct_ReturnsUpdatedProduct_WhenValidData() throws Exception {
                // Arrange
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);

                ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                                .name("Updated Product")
                                .description("Updated Description")
                                .price(199.99)
                                .stock(15)
                                .categories(List.of())
                                .build();

                // Act and Assert
                mockMvc.perform(put("/api/v1/products/{id}", savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", CoreMatchers.is(updateRequest.getName())))
                                .andExpect(jsonPath("$.description", CoreMatchers.is(updateRequest.getDescription())))
                                .andExpect(jsonPath("$.price", CoreMatchers.is(updateRequest.getPrice())))
                                .andExpect(jsonPath("$.stock", CoreMatchers.is(updateRequest.getStock())));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_UpdateProduct_Returns400_WhenInvalidData() throws Exception {
                // Arrange
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);

                ProductRequestDTO invalidRequest = ProductRequestDTO.builder()
                                .name("") // Invalid: empty name
                                .description("Valid Description")
                                .price(-10.0) // Invalid: negative price
                                .stock(-1) // Invalid: negative stock
                                .build();

                // Act and Assert
                mockMvc.perform(put("/api/v1/products/{id}", savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_DeleteProduct_Returns204_WhenSoftDelete() throws Exception {
                // Arrange
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(new ArrayList<>(List.of(category)));
                Product savedProduct = productRepository.save(testProduct);

                // Act and Assert
                mockMvc.perform(delete("/api/v1/products/{id}?force=false", savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_DeleteProduct_Returns204_WhenForceDelete() throws Exception {
                // Arrange
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);

                // Act and Assert
                mockMvc.perform(delete("/api/v1/products/{id}?force=true", savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_GetAllProductImages_ReturnsListOfImages_WhenProductExists() throws Exception {
                // Arrange
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);

                // Act and Assert
                mockMvc.perform(get("/api/v1/products/{id}/productImages", savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$", CoreMatchers.isA(List.class)));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_GetAllProductImages_Returns404_WhenProductNotFound() throws Exception {
                // Arrange
                UUID nonExistentId = UUID.randomUUID();

                // Act and Assert
                mockMvc.perform(get("/api/v1/products/{id}/productImages", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_AddProductImage_Returns201_WhenImageIsSaved() throws Exception {
                // Arrange
                MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", new byte[] { 1, 2, 3 });
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);
                given(storageService.addProductImage(file, savedProduct.getId())).willReturn(new ImageDTO());

                mockMvc.perform(multipart("/api/v1/products/{id}/productImages", savedProduct.getId()).file(file))
                                .andDo(print())
                                .andExpect(status().isCreated());

        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_GetProductReviews_ReturnsListOfReviews_WhenProductExists() throws Exception {
                // Arrange
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);

                // Act and Assert
                mockMvc.perform(get("/api/v1/products/{id}/reviews", savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$", CoreMatchers.isA(List.class)));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ProductController_GetProductReviews_Returns404_WhenProductNotFound() throws Exception {
                // Arrange
                UUID nonExistentId = UUID.randomUUID();

                // Act and Assert
                mockMvc.perform(get("/api/v1/products/{id}/reviews", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }
}
