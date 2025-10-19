package com.kunal.billingSoftware.controller;

import com.kunal.billingSoftware.io.UserRequest;
import com.kunal.billingSoftware.io.UserResponse;
import com.kunal.billingSoftware.service.UserService;
import com.kunal.billingSoftware.service.impl.AppUserDetailsService;
import com.kunal.billingSoftware.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security for testing
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    private final Timestamp fixedTime = Timestamp.valueOf(LocalDateTime.of(2025, 10, 19, 10, 0));

    private final UserRequest userRequest = UserRequest.builder()
            .name("John Doe")
            .email("john.doe@example.com")
            .password("password123")
            .role("USER")
            .build();

    private final UserResponse userResponse = UserResponse.builder()
            .userId("user-123")
            .name("John Doe")
            .email("john.doe@example.com")
            .role("USER")
            .createdAt(fixedTime)
            .updatedAt(fixedTime)
            .build();

    @Test
    void testRegisterUser_ShouldReturnCreatedUser_WhenRequestIsValid() throws Exception {
        when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService, times(1)).createUser(any(UserRequest.class));
    }

    @Test
    void testRegisterUser_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        when(userService.createUser(any(UserRequest.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Unable to create user")));

        verify(userService, times(1)).createUser(any(UserRequest.class));
    }

    @Test
    void testReadUsers_ShouldReturnListOfUsers() throws Exception {
        when(userService.readUsers()).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].role").value("USER"));

        verify(userService, times(1)).readUsers();
    }

    @Test
    void testDeleteUser_ShouldReturnNoContent_WhenUserExists() throws Exception {
        String userId = "user-123";
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/admin/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void testDeleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        String invalidUserId = "invalid-123";
        doThrow(new RuntimeException("User not found"))
                .when(userService).deleteUser(invalidUserId);

        mockMvc.perform(delete("/admin/users/{id}", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("User with ID:")));

        verify(userService, times(1)).deleteUser(invalidUserId);
    }
}
