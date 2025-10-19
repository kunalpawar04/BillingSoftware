package com.kunal.billingSoftware.repository;

import com.kunal.billingSoftware.entity.CategoryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testFindByCategory_WhenCategoryExists() {
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .categoryId("demo-category-id")
                .name("demo-category-name")
                .build();

        categoryRepository.save(categoryEntity);

        Optional<CategoryEntity> response = categoryRepository.findByCategoryId("demo-category-id");

        assertNotNull(response);
        assertEquals("demo-category-id", response.get().getCategoryId());
        assertEquals("demo-category-name", response.get().getName());
    }

    @Test
    void testFindByCategoryId_WhenCategoryDoesNotExist() {
        Optional<CategoryEntity> response = categoryRepository.findByCategoryId("non-existent-id");
        assertTrue(response.isEmpty());
    }
}