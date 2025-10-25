package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.Category;

@DataJpaTest
@ActiveProfiles("test")
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    private final Category testCategory1 = Category.builder()
            .name("Electronics")
            .build();

    private final Category testCategory2 = Category.builder()
            .name("Clothing")
            .build();

    @Test
    public void CategoryRepository_Save_ReturnSavedCategory() {
        // Arrange
        Category category = testCategory1;

        // Act
        Category savedCategory = categoryRepository.save(category);

        // Assert
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory).usingRecursiveComparison().isEqualTo(category);
    }

    @Test
    public void CategoryRepository_FindAll_ReturnMoreThanOneCategory() {
        // Arrange
        Category category1 = testCategory1;
        Category category2 = testCategory2;

        // Act
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        List<Category> savedCategories = categoryRepository.findAll();

        // Assert
        assertThat(savedCategories).isNotNull();
        assertThat(savedCategories.size()).isEqualTo(2);
        assertThat(savedCategories).extracting(Category::getName).containsExactlyInAnyOrder("Electronics", "Clothing");
    }

    @Test
    public void CategoryRepository_FindById_ReturnCategory() {
        // Arrange
        Category category = testCategory1;
        Category savedCategory = categoryRepository.save(category);

        // Act
        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getId());

        // Assert
        assertThat(foundCategory).isPresent();
        Category retrievedCategory = foundCategory.get();
        assertThat(retrievedCategory).isNotNull();
        assertThat(retrievedCategory).usingRecursiveComparison().isEqualTo(savedCategory);
    }

    @Test
    public void CategoryRepository_FindById_ReturnEmpty() {
        // Arrange
        Integer nonExistentId = 999;

        // Act
        Optional<Category> foundCategory = categoryRepository.findById(nonExistentId);

        // Assert
        assertThat(foundCategory).isEmpty();
    }

    @Test
    public void CategoryRepository_FindByName_ReturnCategory() {
        // Arrange
        Category category = testCategory1;
        categoryRepository.save(category);

        // Act
        Optional<Category> foundCategory = categoryRepository.findByName("Electronics");

        // Assert
        assertThat(foundCategory).isPresent();
        Category retrievedCategory = foundCategory.get();
        assertThat(retrievedCategory).isNotNull();
        assertThat(retrievedCategory).usingRecursiveComparison().isEqualTo(category);
        assertThat(retrievedCategory.getName()).isEqualTo("Electronics");
    }

    @Test
    public void CategoryRepository_FindByName_ReturnEmpty() {
        // Arrange
        Category category = testCategory1;
        categoryRepository.save(category);

        // Act
        Optional<Category> foundCategory = categoryRepository.findByName("NonExistent");

        // Assert
        assertThat(foundCategory).isEmpty();
    }

    @Test
    public void CategoryRepository_DeleteById_DeleteCategory() {
        // Arrange
        Category category = testCategory1;
        Category savedCategory = categoryRepository.save(category);

        // Act
        categoryRepository.deleteById(savedCategory.getId());

        // Assert
        assertThat(categoryRepository.existsById(savedCategory.getId())).isFalse();
    }

    @Test
    public void CategoryRepository_Delete_DeleteCategory() {
        // Arrange
        Category category = testCategory1;
        Category savedCategory = categoryRepository.save(category);

        // Act
        categoryRepository.delete(savedCategory);

        // Assert
        assertThat(categoryRepository.existsById(savedCategory.getId())).isFalse();
    }
}
