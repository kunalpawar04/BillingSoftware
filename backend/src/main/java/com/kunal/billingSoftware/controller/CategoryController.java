package com.kunal.billingSoftware.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kunal.billingSoftware.exceptions.ResourceNotFoundException;
import com.kunal.billingSoftware.io.CategoryRequest;
import com.kunal.billingSoftware.io.CategoryResponse;
import com.kunal.billingSoftware.service.CategoryService;
import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse addCategory(@RequestPart("category") String categoryString,
                                        @RequestPart("file") MultipartFile categoryImg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CategoryRequest request = objectMapper.readValue(categoryString, CategoryRequest.class);

            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                for (ConstraintViolation<CategoryRequest> violation : violations) {
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                }
                throw new ConstraintViolationException(errors.toString(), violations);
            }

            return categoryService.add(request, categoryImg);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while processing the image: " + ex.getMessage());
        }
    }

    @GetMapping("/categories")
    public List<CategoryResponse> getCategories() {
        return categoryService.getAll();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/categories/{categoryId}")
    public void deleteCategory(@PathVariable String categoryId) {
        categoryService.delete(categoryId);
    }
}
