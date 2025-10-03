package com.kunal.billingSoftware.service.impl;

import com.kunal.billingSoftware.entity.UserEntity;
import com.kunal.billingSoftware.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Explicit constructor injection
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void getUserRole_shouldReturnRole_whenUserExists() {
        // Arrange
        String email = "test@example.com";
        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .role("ADMIN")
                .name("KP")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));

        // Act
        String role = userService.getUserRole(email);


        // Assert
//        assertEquals("ADMIN", role);
        assertThat(role).isEqualTo("ADMIN");
        verify(userRepository, times(2)).findByEmail(email);
    }
}