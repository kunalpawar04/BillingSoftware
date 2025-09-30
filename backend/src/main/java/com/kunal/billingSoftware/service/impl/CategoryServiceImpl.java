package com.kunal.billingSoftware.service.impl;

import com.kunal.billingSoftware.entity.CategoryEntity;
import com.kunal.billingSoftware.io.CategoryRequest;
import com.kunal.billingSoftware.io.CategoryResponse;
import com.kunal.billingSoftware.repository.CategoryRepository;
import com.kunal.billingSoftware.repository.ItemRepository;
import com.kunal.billingSoftware.service.CategoryService;
import com.kunal.billingSoftware.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final FileUploadService fileUploadService;

    private final ItemRepository itemRepository;

    @Override
    public CategoryResponse add(CategoryRequest request, MultipartFile file) {
        String imgUrl = fileUploadService.uploadFile(file);
        CategoryEntity newCategory = convertToEntity(request);
        newCategory.setImageUrl(imgUrl);
        newCategory = categoryRepository.save(newCategory);
        return convertToResponse(newCategory);
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryEntity -> convertToResponse(categoryEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String categoryId) {
        CategoryEntity category = categoryRepository.findByCategoryId(categoryId).orElseThrow(() -> new RuntimeException("Category with ID: " + categoryId + " does not exist"));
        fileUploadService.deleteFile(category.getImageUrl());
        categoryRepository.deleteById(category.getId());
    }

    private CategoryResponse convertToResponse(CategoryEntity newCategory) {
        Integer totalItems = itemRepository.countByCategoryId(newCategory.getId());
        return CategoryResponse.builder()
                .name(newCategory.getName())
                .description(newCategory.getDescription())
                .categoryId(newCategory.getCategoryId())
                .bgColor(newCategory.getBgColor())
                .imageUrl(newCategory.getImageUrl())
                .createdAt(newCategory.getCreatedAt())
                .updatedAt(newCategory.getUpdatedAt())
                .items(totalItems)
                .build();
    }

    private CategoryEntity convertToEntity(CategoryRequest request) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .build();
    }
}
