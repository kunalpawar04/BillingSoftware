package com.kunal.billingSoftware.controller;

import com.kunal.billingSoftware.io.CategoryRequest;
import com.kunal.billingSoftware.io.CategoryResponse;
import com.kunal.billingSoftware.service.CategoryService;
import com.kunal.billingSoftware.service.impl.AppUserDetailsService;
import com.kunal.billingSoftware.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CategoryService categoryService;

    private Timestamp fixedTime = Timestamp.valueOf(LocalDateTime.of(2025, 10, 17, 7, 0));

    @Test
    void addCategory_ShouldReturnCreatedCategory_WhenValidRequest() throws Exception {
        // Given
        String categoryJson = """
                {
                  "name": "Demo-category",
                  "description": "Demo description",
                  "bgColor": "#ffffff"
                }
                """;

        MockMultipartFile categoryPart =
                new MockMultipartFile("category", "", "application/json", categoryJson.getBytes());

        MockMultipartFile imagePart =
                new MockMultipartFile("file", "image.png", "image/png", "fake-image-data".getBytes());

        CategoryResponse response = CategoryResponse.builder()
                .categoryId("123")
                .name("Demo-category")
                .description("Demo description")
                .bgColor("#ffffff")
                .imageUrl("test-url")
                .build();

        when(categoryService.add(any(CategoryRequest.class), any(MultipartFile.class)))
                .thenReturn(response);

        // When + Then
        mockMvc.perform(multipart("/admin/categories")
                        .file(categoryPart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value("123"))
                .andExpect(jsonPath("$.name").value("Demo-category"))
                .andExpect(jsonPath("$.imageUrl").value("test-url"));
    }

    @Test
    void testAddCategory_ShouldReturnBadRequest_WhenJsonIsInvalid() throws Exception {
        String invalidCategoryJson = """
            {
              "name": "Demo-category",
              "description": "Unclosed json description,
              "bgColor": "#ffffff"
            }
            """;

        MockMultipartFile invalidCategoryPart =
                new MockMultipartFile("category", "", "application/json", invalidCategoryJson.getBytes());

        MockMultipartFile imagePart =
                new MockMultipartFile("file", "image.png", "image/png", "fake-image-data".getBytes());

        mockMvc.perform(
                        multipart("/admin/categories")
                                .file(invalidCategoryPart)
                                .file(imagePart)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result ->
                        assertTrue(
                                result.getResolvedException()
                                        .getMessage()
                                        .contains("Error while processing the image"),
                                "Expected error message to mention image processing"
                        ));
    }

    @Test
    void getCategories_ShouldReturnListOfCategories() throws Exception {
        // Given
        CategoryResponse category1 = CategoryResponse.builder()
                .categoryId("1")
                .name("Groceries")
                .description("Daily needs")
                .bgColor("#ffffff")
                .imageUrl("url1")
                .build();

        CategoryResponse category2 = CategoryResponse.builder()
                .categoryId("2")
                .name("Electronics")
                .description("Gadgets and devices")
                .bgColor("#000000")
                .imageUrl("url2")
                .build();

        List<CategoryResponse> mockResponse = List.of(category1, category2);

        when(categoryService.getAll()).thenReturn(mockResponse);

        // When + Then
        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value("1"))
                .andExpect(jsonPath("$[0].name").value("Groceries"))
                .andExpect(jsonPath("$[1].categoryId").value("2"))
                .andExpect(jsonPath("$[1].name").value("Electronics"));
    }

    @Test
    void testDeleteCategory_ShouldDeleteCategory_WhenCategoryIdExist() throws Exception {
        String categoryId = "demo-id-123";
        doNothing().when(categoryService).delete(categoryId);

        // Act + Assert
        mockMvc.perform(delete("/admin/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify that the service method was called once
        verify(categoryService, times(1)).delete(categoryId);
    }

    @Test
    void testDeleteCategory_ShouldReturnException_WhenCategoryIdDoesNotExist() throws Exception {
        // Arrange
        doThrow(ResponseStatusException.class)
                .when(categoryService)
                .delete(anyString());

        // Act + Assert
        mockMvc.perform(delete("/admin/categories/{categoryId}", "invalid-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException));
    }
}