package com.kunal.billingSoftware.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.kunal.billingSoftware.entity.UserEntity;
import com.kunal.billingSoftware.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
class AppUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Scenario 1 - Happy path → When a user is found by email.
    @Test
    void loadUserByUsername_WhenUserExists_ReturnsUserDetails() {
        // Arrange
        UserEntity mockUser = new UserEntity();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password@123");
        mockUser.setRole("ROLE_USER");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = appUserDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password@123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    // Scenario 2 - Failure path → When no user is found (should throw UsernameNotFoundException).
    @Test
    void loadUserByUserDetails_WhenUserDoesNotExist_ReturnsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            appUserDetailsService.loadUserByUsername("missing@example.com");
        });

        // Also verify repository was called
        verify(userRepository).findByEmail("missing@example.com");
    }
}