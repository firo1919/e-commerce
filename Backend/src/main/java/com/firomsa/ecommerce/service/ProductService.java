package com.firomsa.ecommerce.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.dto.CategoryRequestDTO;
import com.firomsa.ecommerce.dto.ProductRequestDTO;
import com.firomsa.ecommerce.dto.ProductResponseDTO;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.mapper.ProductMapper;
import com.firomsa.ecommerce.model.Category;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.repository.CategoryRepository;
import com.firomsa.ecommerce.repository.ProductRepository;

import jakarta.transaction.Transactional;
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductResponseDTO> getAll() {
        return productRepository.findAll().stream().map(ProductMapper::toDTO).toList();
    }

    public ProductResponseDTO get(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + id.toString()));
        return ProductMapper.toDTO(product);
    }

    @Transactional
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

    public void remove(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " +
                        id.toString()));
        productRepository.delete(product);
    }

    @Transactional
    public void softDelete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + id.toString()));
        product.setActive(false);
        productRepository.save(product);
    }

}
