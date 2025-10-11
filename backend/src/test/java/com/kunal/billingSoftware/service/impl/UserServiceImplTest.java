package com.kunal.billingSoftware.service.impl;

import com.kunal.billingSoftware.entity.UserEntity;
import com.kunal.billingSoftware.io.UserRequest;
import com.kunal.billingSoftware.io.UserResponse;
import com.kunal.billingSoftware.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;
    private UserEntity userEntity;
    private UserResponse userResponse;
    private UserRequest userRequest;
    private Timestamp fixedTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Explicit constructor injection
        userService = new UserServiceImpl(userRepository, passwordEncoder);

        fixedTime = Timestamp.valueOf(LocalDateTime.of(2025, 10, 10, 10, 0));

        // Shared userEntity setup
        userEntity = UserEntity.builder()
                .userId("123")
                .name("Kunal Pawar")
                .email("kp@gmail.com")
                .role("ADMIN")
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();

        userRequest = UserRequest.builder()
                .email("kp@gmail.com")
                .password("normal-password")
                .role("ADMIN")
                .name("Kunal Pawar")
                .build();

        userResponse = UserResponse.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .userId(userEntity.getUserId())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .role(userEntity.getRole())
                .build();
    }

    @Test
    void testCreateUser_ShouldReturnCreateUser_WhenUserIsCreatedSuccessfully() {
        // Arrange
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Act
        UserResponse response = userService.createUser(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(userEntity.getUserId(), response.getUserId());
        assertEquals(userEntity.getName(), response.getName());
        assertEquals(userEntity.getEmail(), response.getEmail());
        assertEquals(userEntity.getRole(), response.getRole());

        verify(passwordEncoder, times(1)).encode(userRequest.getPassword());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testGetUserRole_ShouldReturnRole_whenUserExists() {
        // Arrange
        String email = "kp@gmail.com";
        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .role("ADMIN")
                .name("Kunal Pawar")
                .build();

        when(userRepository.findByEmail("kp@gmail.com")).thenReturn(Optional.of(userEntity));

        // Act
        String role = userService.getUserRole(email);


        // Assert
        // assertEquals("ADMIN", role);
        assertThat(role).isEqualTo("ADMIN");
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetUserRole_ShouldThrowUsernameNotFoundException_whenUserDoesNotExist() {
        // Arrange
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act + Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.getUserRole(email)
        );

        assertEquals("User not found for the email: " + email, exception.getMessage());
        verify(userRepository).findByEmail(email); // optional but good practice
    }

    @Test
    void testReadUsers_ShouldReturnList_WhenUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(userEntity));

        // Act
        var response = userService.readUsers();

        // Assert
        assertEquals(1, response.size());
        assertEquals("123", response.get(0).getUserId());
        assertEquals("Kunal Pawar", response.get(0).getName());
        assertEquals("kp@gmail.com", response.get(0).getEmail());
        assertEquals("ADMIN", response.get(0).getRole());
    }

    @Test
    void testReadUsers_ShouldReturnEmpty_WhenNoUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        var response = userService.readUsers();

        // Assert
        assertNotNull(response);
        assertEquals(0, response.size());

        // Check if method was called at least once
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testDeleteUser_ShouldDeleteUser_WhenUserIdExist() {
        // Arrange
        when(userRepository.findByUserId(userEntity.getUserId())).thenReturn(Optional.of(userEntity));

        // Act
        userService.deleteUser(userEntity.getUserId());

        // Assert
        verify(userRepository, times(1)).findByUserId(userEntity.getUserId());
        verify(userRepository, times(1)).delete(userEntity);
    }

    @Test
    void testDeleteUser_ShouldThrowUsernameNotFoundException_WhenUserIdDoesNotExist() {
        // Arrange
        String userId = "missing-id";
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act + Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.deleteUser(userId));

        // Verify
        assertEquals("User not found for the email: " + userId, exception.getMessage());
        verify(userRepository, never()).delete(any());
    }
}