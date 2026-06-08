package com.phongvu.restapi.service;

import com.phongvu.restapi.dto.request.UserCreationRequest;
import com.phongvu.restapi.dto.request.UserUpdateRequest;
import com.phongvu.restapi.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserService {
    UserResponse createUser(UserCreationRequest request);
    List<UserResponse> getAllUser();
    Page<UserResponse> getPage(int page, int size);
    UserResponse getUserById(String id);
    UserResponse getProfile();
    UserResponse updateUser(String id, UserUpdateRequest request);
    void deleteUser(String id);
}

