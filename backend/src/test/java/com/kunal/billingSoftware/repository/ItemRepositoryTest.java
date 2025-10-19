package com.kunal.billingSoftware.repository;

import com.kunal.billingSoftware.entity.CategoryEntity;
import com.kunal.billingSoftware.entity.ItemEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testFindByItemId_WhenItemExists() {
        CategoryEntity demoCategory = CategoryEntity.builder()
                .categoryId("cat-001")
                .name("Electronics")
                .description("Good product")
                .bgColor("#ffffff")
                .imageUrl("img.png")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        categoryRepository.save(demoCategory);

        ItemEntity demoItem = ItemEntity.builder()
                .itemId("item-001")
                .name("Wireless Mouse")
                .price(BigDecimal.valueOf(25.99))
                .description("Ergonomic wireless mouse")
                .imgUrl("mouse.png")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .category(demoCategory) // existing persisted category
                .build();

        itemRepository.save(demoItem);

        Optional<ItemEntity> response = itemRepository.findByItemId("item-001");

        assertTrue(response.isPresent());
        assertEquals("Wireless Mouse", response.get().getName());
        assertEquals("cat-001", response.get().getCategory().getCategoryId());
    }

    @Test
    void testFindByItemId_WhenItemDoesNotExist() {
        Optional<ItemEntity> response = itemRepository.findByItemId("invalid-id");

        assertTrue(response.isEmpty());
    }

    @Test
    void testCountByCategoryId_ShouldReturnCorrectCount() {
        CategoryEntity category = CategoryEntity.builder()
                .categoryId("cat-001")
                .name("Electronics")
                .description("Category for electronic products")
                .bgColor("#ffffff")
                .imageUrl("electronics.png")
                .build();

        categoryRepository.save(category);

        ItemEntity item1 = ItemEntity.builder()
                .itemId("item-001")
                .name("Keyboard")
                .price(BigDecimal.valueOf(29.99))
                .category(category)
                .build();

        ItemEntity item2 = ItemEntity.builder()
                .itemId("item-002")
                .name("Mouse")
                .price(BigDecimal.valueOf(19.99))
                .category(category)
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        Integer count = itemRepository.countByCategoryId(category.getId());

        assertEquals(2, count);
    }

    @Test
    void testCountByCategoryId_ShouldReturnZero_WhenNoItemsExist() {
        CategoryEntity category = CategoryEntity.builder()
                .categoryId("cat-002")
                .name("Furniture")
                .description("Category for furniture items")
                .build();

        categoryRepository.save(category);

        Integer count = itemRepository.countByCategoryId(category.getId());

        assertEquals(0, count);
    }
}