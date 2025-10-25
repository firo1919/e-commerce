package com.firomsa.ecommerce.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firomsa.ecommerce.model.Category;
import com.firomsa.ecommerce.repository.CategoryRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CategoriesLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    public CategoriesLoader(CategoryRepository categoryRepository, ObjectMapper objectMapper) {
        this.categoryRepository = categoryRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            try {
                // Load the JSON file from resources
                var resource = getClass().getClassLoader().getResourceAsStream("data/categories.json");
                if (resource == null) {
                    log.warn("Could not find categories.json in resources.");
                    return;
                }

                // Create a type reference for a List of Map
                java.util.List<java.util.Map<String, String>> categoriesList = objectMapper.readValue(
                        resource,
                        objectMapper.getTypeFactory().constructCollectionType(java.util.List.class,
                                java.util.Map.class));

                for (var categoryMap : categoriesList) {
                    String categoryName = categoryMap.get("name");
                    var category = Category.builder()
                            .name(categoryName)
                            .build();
                    categoryRepository.save(category);
                }
                log.info("Categories loaded successfully from JSON.");
            } catch (Exception e) {
                log.error("Failed to load categories from JSON", e);
            }
        }
    }

}
