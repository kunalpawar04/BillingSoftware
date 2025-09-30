package com.kunal.billingSoftware.service;

import com.kunal.billingSoftware.io.UserRequest;
import com.kunal.billingSoftware.io.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);

    String getUserRole(String email);

    List<UserResponse> readUsers();

    void deleteUser(String id);
}
