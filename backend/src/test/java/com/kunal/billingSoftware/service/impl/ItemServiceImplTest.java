package com.kunal.billingSoftware.service.impl;

import com.kunal.billingSoftware.entity.CategoryEntity;
import com.kunal.billingSoftware.entity.ItemEntity;
import com.kunal.billingSoftware.io.ItemRequest;
import com.kunal.billingSoftware.io.ItemResponse;
import com.kunal.billingSoftware.repository.CategoryRepository;
import com.kunal.billingSoftware.repository.ItemRepository;
import com.kunal.billingSoftware.service.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.parser.Entity;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private FileUploadService fileUploadService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    MultipartFile file;

    private ItemServiceImpl itemService;
    private ItemResponse itemResponse;
    private ItemRequest itemRequest;
    private ItemEntity itemEntity;
    private Timestamp fixedTime;
    private CategoryEntity category;

    @BeforeEach
    void setup() {
        itemService = new ItemServiceImpl(fileUploadService, categoryRepository, itemRepository);

        fixedTime = Timestamp.valueOf(LocalDateTime.of(2025, 10, 10, 10, 0));

        category = CategoryEntity.builder()
                .categoryId("category-id-123")
                .name("Demo category")
                .build();

        itemRequest = ItemRequest.builder()
                .name("Demo Mobile")
                .description("Demo desc")
                .price(new BigDecimal(100.00))
                .categoryId("category-id-123")
                .build();

        itemEntity = ItemEntity.builder()
                .itemId(UUID.randomUUID().toString())
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .imgUrl("img-url")          // add this
                .category(category)
                .price(itemRequest.getPrice())
                .build();

        itemResponse = ItemResponse.builder()
                .itemId(itemEntity.getItemId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .price(itemEntity.getPrice())
                .imageUrl("img-url")
                .categoryName("Demo category")
                .categoryId("category-id-123")
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();
    }

    @Test
    void testAddItem_WhenItemRequestAndFileIsValid_Success() {
        // Arrange
        when(fileUploadService.uploadFile(file)).thenReturn("image-url");
        when(categoryRepository.findByCategoryId("category-id-123")).thenReturn(Optional.of(category));
        when(itemRepository.save(any(ItemEntity.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ItemResponse newItemResponse = itemService.add(itemRequest, file);

        // Verify
        assertTrue(("Demo Mobile").equals(newItemResponse.getName()));
        assertEquals("image-url", newItemResponse.getImageUrl());
        assertEquals("category-id-123", newItemResponse.getCategoryId());

        verify(fileUploadService, times(1)).uploadFile(file);
        verify(categoryRepository, times(1)).findByCategoryId("category-id-123");
        verify(itemRepository, times(1)).save(any(ItemEntity.class));
    }

    @Test
    void testAddItem_WhenCategoryIdDoesNotExist_Failure() {
        // Arrange
        when(fileUploadService.uploadFile(file)).thenReturn("image-url");
        when(categoryRepository.findByCategoryId(anyString())).thenReturn(Optional.empty());

        // Assert + Act
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> itemService.add(itemRequest, file));
        System.out.println(runtimeException.getMessage());

        // Verify
        assertEquals(runtimeException.getMessage(), "Category with ID: " + category.getCategoryId() + " not found");
        verify(fileUploadService, times(1)).uploadFile(file);
        verify(categoryRepository, times(1)).findByCategoryId(anyString());
        verify(itemRepository, times(0)).save(any(ItemEntity.class));
    }

    @Test
    void testFetchItems_ShouldReturnList_WhenItemsExist() {
        // Arrange
        when(itemRepository.findAll()).thenReturn(List.of(itemEntity));

        // Act
        var response = itemService.fetchItems();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(itemEntity.getItemId(), response.get(0).getItemId());
        assertEquals(itemEntity.getPrice(), response.get(0).getPrice());
        assertEquals("img-url", response.get(0).getImageUrl());
        assertEquals("Demo category", response.get(0).getCategoryName());
        assertEquals("category-id-123", response.get(0).getCategoryId());

        // verify
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testFetchItems_ShouldReturnEmptyList_WhenItemsDoNotExist() {
        // Arrange
        when(itemRepository.findAll()).thenReturn(List.of());

        // Act
        var response = itemService.fetchItems();

        // Assert
        assertNotNull(response);
        assertEquals(0, response.size());

        // verify
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testDeleteItem_ShouldDeleteItem_WhenItemIdExist() {
        // Arrange
        when(itemRepository.findByItemId(anyString())).thenReturn(Optional.of(itemEntity));
        when(fileUploadService.deleteFile(anyString())).thenReturn(true);
        doNothing().when(itemRepository).delete(itemEntity);

        // Act
        itemService.deleteItem(anyString());

        // Assert + Verify
        verify(itemRepository, times(1)).findByItemId(anyString());
        verify(fileUploadService, times(1)).deleteFile(anyString());
        verify(itemRepository, times(1)).delete(itemEntity);
    }

    @Test
    void testDeleteItem_ShouldThrowRuntimeException_WhenItemIdDoesNotExist() {
        // Arrange
        when(itemRepository.findByItemId(anyString())).thenReturn(Optional.empty());

        // Act
        String invalidItemId = "invalid-item-id";
        RuntimeException exception = assertThrows(RuntimeException.class, () -> itemService.deleteItem(invalidItemId));

        // Assert
        assertEquals("Item with ID: " + invalidItemId + " not found", exception.getMessage());

        // Verify
        verify(itemRepository, times(1)).findByItemId(anyString());
        verify(fileUploadService, times(0)).deleteFile(anyString());
        verify(itemRepository, times(0)).delete(itemEntity);
    }
}