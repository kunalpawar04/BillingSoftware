package com.kunal.billingSoftware.service.impl;

import com.kunal.billingSoftware.entity.CategoryEntity;
import com.kunal.billingSoftware.io.CategoryRequest;
import com.kunal.billingSoftware.io.CategoryResponse;
import com.kunal.billingSoftware.repository.CategoryRepository;
import com.kunal.billingSoftware.repository.ItemRepository;
import com.kunal.billingSoftware.service.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Array;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryEntity categoryEntity;
    private CategoryRequest categoryRequest;


    @BeforeEach
    void setUp() {

        // MockitoAnnotations.openMocks(this);

        categoryRequest = CategoryRequest.builder()
                .name("Electronics")
                .description("Electronics items")
                .bgColor("#ffffff")
                .build();

        categoryEntity = CategoryEntity.builder()
                .id(1L)
                .categoryId(UUID.randomUUID().toString())
                .name("Electronics")
                .description("Electronic items")
                .bgColor("#FFFFFF")
                .imageUrl("img-url.png")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }

    @Test
    void testAddCategory_Success() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(fileUploadService.uploadFile(file)).thenReturn("img-url.png");
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(categoryEntity);
        when(itemRepository.countByCategoryId(anyLong())).thenReturn(0);

        // Act
        CategoryResponse categoryResponse = categoryService.add(categoryRequest, file);

        // Assert
        assertNotNull(categoryResponse);
        assertTrue("Electronics".equals(categoryResponse.getName()));
        assertEquals("img-url.png", categoryResponse.getImageUrl());

        // Checking if method is called at least once
        verify(fileUploadService).uploadFile(file);
        verify(categoryRepository).save(any(CategoryEntity.class));
        verify(itemRepository).countByCategoryId(anyLong());
    }

    @Test
    void testGetAllCategories_Success() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(categoryEntity));
        when(itemRepository.countByCategoryId(categoryEntity.getId())).thenReturn(5);

        // Act
        var response = categoryService.getAll();

        // Assert
        assertTrue(1 == response.size());
        assertEquals("Electronics", response.get(0).getName());
        assertEquals(5, response.get(0).getItems());
    }

    @Test
    void testDeleteCategory_WhenCategoryIdExist_Success() {
        // Arrange
        when(categoryRepository.findByCategoryId(categoryEntity.getCategoryId()))
                .thenReturn(Optional.of(categoryEntity));

        when(fileUploadService.deleteFile(anyString())).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(anyLong());
        // Act
        categoryService.delete(categoryEntity.getCategoryId());

        // Assert
        verify(fileUploadService).deleteFile(categoryEntity.getImageUrl());
        verify(categoryRepository).deleteById(categoryEntity.getId());
    }

    @Test
    void testDeleteCategory_WhenCategoryIdDoesNotExist_ReturnsRuntimeException() {
        // Arrange
        String categoryId = "invalid-id";
        when(categoryRepository.findByCategoryId(categoryId)).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> categoryService.delete(categoryId));

        assertEquals(("Category with ID: " + categoryId + " does not exist"), exception.getMessage());

        // Checking if method is never called
        verify(fileUploadService, never()).deleteFile(anyString());
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}