package com.kunal.billingSoftware.repository;
import com.kunal.billingSoftware.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail_WhenUserExists() {
        UserEntity user = UserEntity.builder()
                .userId("user-001")
                .email("user@example.com")
                .password("securePassword")
                .role("ADMIN")
                .name("John Doe")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        userRepository.save(user);

        Optional<UserEntity> response = userRepository.findByEmail("user@example.com");

        assertTrue(response.isPresent());
        assertEquals("user@example.com", response.get().getEmail());
        assertEquals("ADMIN", response.get().getRole());
        assertEquals("John Doe", response.get().getName());
    }

    @Test
    void testFindByEmail_WhenUserDoesNotExist() {
        Optional<UserEntity> response = userRepository.findByEmail("invalid@gmail.com");

        assertTrue(response.isEmpty());
    }

    @Test
    void testFindByUserId_WhenUserExist() {
        UserEntity user = UserEntity.builder()
                .userId("user-001")
                .email("user@example.com")
                .password("securePassword")
                .role("ADMIN")
                .name("John Doe")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        userRepository.save(user);

        Optional<UserEntity> response = userRepository.findByUserId("user-001");

        assertTrue(response.isPresent());
        assertEquals("user@example.com", response.get().getEmail());
        assertEquals("ADMIN", response.get().getRole());
        assertEquals("John Doe", response.get().getName());
    }

    @Test
    void testFindByUserId_WhenUserDoesNotExist() {
        Optional<UserEntity> response = userRepository.findByUserId("invalid-user-id");

        assertTrue(response.isEmpty());
    }
}