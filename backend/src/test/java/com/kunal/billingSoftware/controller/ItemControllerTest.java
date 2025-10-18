package com.kunal.billingSoftware.controller;

import com.kunal.billingSoftware.io.ItemRequest;
import com.kunal.billingSoftware.io.ItemResponse;
import com.kunal.billingSoftware.service.ItemService;
import com.kunal.billingSoftware.service.impl.AppUserDetailsService;
import com.kunal.billingSoftware.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class ItemControllerTest {
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private AppUserDetailsService appUserDetailsService;
    @MockitoBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    private Timestamp fixedTime = Timestamp.valueOf(LocalDateTime.of(2025, 10, 18, 13, 13));

    private ItemResponse itemResponse = ItemResponse.builder()
            .itemId("item-id-123")
            .name("test-item-name")
            .price(BigDecimal.valueOf(100000))
            .categoryId("test-category-id")
            .description("test-category-description")
            .categoryName("test-category-name")
            .imageUrl("img-url.png")
            .createdAt(fixedTime)
            .updatedAt(fixedTime)
            .build();

    @Test
    void testAddItem_ShouldReturnIsCreatedStatus_WhenDataExists() throws Exception {
        String itemJson = """
                {
                    "name": "test-item-name",
                    "price": "100000", 
                    "categoryId": "test-category-id", 
                    "description": "test-category-description"
                }
                """;

        MockMultipartFile itemPart = new MockMultipartFile("item", "", "application/json", itemJson.getBytes());
        MockMultipartFile imagePart = new MockMultipartFile("file", "img-url.png", "image/png", "fake-image-data".getBytes());

        when(itemService.add(any(ItemRequest.class), any(MultipartFile.class))).thenReturn(itemResponse);

        mockMvc.perform(multipart("/admin/items")
                .file(itemPart)
                .file(imagePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value("item-id-123"))
                .andExpect(jsonPath("$.name").value("test-item-name"))
                .andExpect(jsonPath("$.price").value("100000"))
                .andExpect(jsonPath("$.categoryId").value("test-category-id"))
                .andExpect(jsonPath("$.description").value("test-category-description"))
                .andExpect(jsonPath("$.categoryName").value("test-category-name"))
                .andExpect(jsonPath("$.imageUrl").value("img-url.png"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    void testAddItem_ShouldReturnBadRequest_WhenJsonIsInvalid() throws Exception {
        String invalidItemJson = """
                {
                    "name": "test-item-name
                    "price": "100000", 
                    "categoryId": "test-category-id", 
                    "description": "test-category-description"
                }
                """;

        MockMultipartFile itemPart = new MockMultipartFile("item", "", "application/json", invalidItemJson.getBytes());
        MockMultipartFile imagePart = new MockMultipartFile("file", "img-url.png", "image/png", "fake-image-data".getBytes());

        mockMvc.perform(multipart("/admin/items")
                .file(itemPart)
                .file(imagePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(response -> assertTrue(response.getResolvedException() instanceof ResponseStatusException))
                .andExpect(response -> assertTrue(
                        response.getResolvedException().getMessage().equals("Error occured while processing the json")
                ));
    }

    @Test
    void testReadItems_ShouldReturnItemList_WhenItemsExist() throws Exception {
        when(itemService.fetchItems()).thenReturn(List.of(itemResponse));

        mockMvc.perform(get("/items").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId").value("item-id-123"))
                .andExpect(jsonPath("$[0].name").value("test-item-name"))
                .andExpect(jsonPath("$[0].price").value(100000))
                .andExpect(jsonPath("$[0].categoryId").value("test-category-id"))
                .andExpect(jsonPath("$[0].description").value("test-category-description"))
                .andExpect(jsonPath("$[0].categoryName").value("test-category-name"))
                .andExpect(jsonPath("$[0].imageUrl").value("img-url.png"))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
    }

    @Test
    void testDeleteItem_ShouldDeleteItem_WhenItemExists() throws Exception {
        String itemId = "test-item-id";

        doNothing().when(itemService).deleteItem(itemId);

        mockMvc.perform(delete("/admin/items/{itemId}", itemId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteItem(itemId);
    }

    @Test
    void testDeleteItem_ShouldReturnNotFoundResponse_WhenItemDoesNotExist() throws Exception {
        String invalidItemId = "invalid-item-id";

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Item with ID: " + invalidItemId + " not found"))
                .when(itemService)
                .deleteItem(invalidItemId);

        mockMvc.perform(delete("/admin/items/{itemId}", invalidItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(
                        result.getResolvedException().getMessage().contains("Item with ID: " + invalidItemId)
                ));
    }
}