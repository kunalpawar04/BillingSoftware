package com.kunal.billingSoftware.service;

import com.kunal.billingSoftware.io.CategoryRequest;
import com.kunal.billingSoftware.io.CategoryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    CategoryResponse add(CategoryRequest request, MultipartFile file);

    List<CategoryResponse> getAll();

    void delete(String categoryId);
}
