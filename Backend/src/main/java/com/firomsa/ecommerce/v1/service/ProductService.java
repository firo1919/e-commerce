package com.firomsa.ecommerce.v1.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.model.Category;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.repository.CategoryRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.v1.dto.CategoryRequestDTO;
import com.firomsa.ecommerce.v1.dto.ProductRequestDTO;
import com.firomsa.ecommerce.v1.dto.ProductResponseDTO;
import com.firomsa.ecommerce.v1.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.v1.mapper.ProductMapper;
import com.firomsa.ecommerce.v1.mapper.ReviewMapper;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<ProductResponseDTO> getAll() {
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .map(ProductMapper::toDTO).toList();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<ProductResponseDTO> getAllInActiveProducts() {
        return productRepository.findAll().stream()
                .filter(e -> (!e.isActive()))
                .map(ProductMapper::toDTO).toList();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ProductResponseDTO get(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + id.toString()));
        return ProductMapper.toDTO(product);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO create(ProductRequestDTO productRequestDTO) {
        Product product = ProductMapper.toModel(productRequestDTO);
        List<CategoryRequestDTO> requestCategories = productRequestDTO.getCategories();
        List<Category> categories = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (CategoryRequestDTO cat : requestCategories) {
            Optional<Category> category = categoryRepository.findByName(cat.getName());
            if (category.isEmpty()) {
                throw new ResourceNotFoundException("Category: " + cat.getName());
            }
            categories.add(category.get());
        }
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        product.setCategories(categories);
        return ProductMapper.toDTO(productRepository.save(product));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO update(ProductRequestDTO productRequestDTO, UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + id.toString()));

        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setStock(productRequestDTO.getStock());
        List<CategoryRequestDTO> requestCategories = productRequestDTO.getCategories();
        List<Category> categories = new ArrayList<>();

        for (CategoryRequestDTO cat : requestCategories) {
            Optional<Category> category = categoryRepository.findByName(cat.getName());
            if (category.isEmpty()) {
                throw new ResourceNotFoundException("Category: " + cat.getName());
            }
            categories.add(category.get());
        }
        product.setCategories(categories);
        product.setUpdatedAt(LocalDateTime.now());

        return ProductMapper.toDTO(productRepository.save(product));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void remove(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " +
                        id.toString()));
        productRepository.delete(product);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void softDelete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + id.toString()));
        product.setActive(false);
        productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<ReviewResponseDTO> getReviews(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + id.toString()));
        return product.getReviews().stream().map(ReviewMapper::toDTO).toList();
    }

}
