package com.phongvu.restapi.service;

import com.phongvu.restapi.dto.request.UserCreationRequest;
import com.phongvu.restapi.dto.request.UserUpdateRequest;
import com.phongvu.restapi.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);
    List<UserResponse> getAllUser();
    UserResponse getUserById(String id);
    UserResponse getProfile();
    UserResponse updateUser(String id, UserUpdateRequest request);
    void deleteUser(String id);
}
