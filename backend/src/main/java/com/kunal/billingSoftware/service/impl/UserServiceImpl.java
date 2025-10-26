package com.kunal.billingSoftware.service.impl;

import com.kunal.billingSoftware.entity.UserEntity;
import com.kunal.billingSoftware.exceptions.ResourceNotFoundException;
import com.kunal.billingSoftware.exceptions.UserCreationException;
import com.kunal.billingSoftware.io.UserRequest;
import com.kunal.billingSoftware.io.UserResponse;
import com.kunal.billingSoftware.projection.UserProjection;
import com.kunal.billingSoftware.repository.UserRepository;
import com.kunal.billingSoftware.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {
        try {
            UserEntity newUser = convertToEntity(request);
            newUser = userRepository.save(newUser);
            return convertToResponse(newUser);
        } catch (Exception ex) {
            throw new UserCreationException("Unable to create user. " + ex.getMessage());
        }
    }

    private UserResponse convertToResponse(UserEntity newUser) {
        return UserResponse.builder()
                .name(newUser.getName())
                .email(newUser.getEmail())
                .userId(newUser.getUserId())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .role(newUser.getRole())
                .build();
    }

    private UserEntity convertToEntity(UserRequest request) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase())
                .name(request.getName())
                .build();
    }

    @Override
    public String getUserRole(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return existingUser.getRole();
    }

    @Override
    public List<UserResponse> readUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> convertToResponse(user))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(String id) {
        UserEntity existingUser = userRepository.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(existingUser);
    }

    @Override
    public List<UserProjection> getAllUsersBasicInfo() {
        return userRepository.findAllProjectedBy();
    }
}
