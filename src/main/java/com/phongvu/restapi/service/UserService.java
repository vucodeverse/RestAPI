package com.phongvu.restapi.service;

import com.phongvu.restapi.constants.ApiMessage;
import com.phongvu.restapi.dto.request.AuthenticationRequest;
import com.phongvu.restapi.dto.request.UserCreationRequest;
import com.phongvu.restapi.dto.request.UserUpdateRequest;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.UserResponse;
import com.phongvu.restapi.mapper.UserMapper;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.repository.UserRepo;
import com.phongvu.restapi.utils.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;


    public User createRequestUser(UserCreationRequest request) {

        if (userRepo.existsByUsername(request.getUsername()))
            throw new AppException(ApiMessage.USER_EXITED);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepo.save(user);
    }

    public List<User> getAllUser() {
        return userRepo.findAll();
    }

    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepo.findById(id)
                .orElseThrow(() -> new AppException(ApiMessage.USER_NOT_FOUND)));
    }

    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new AppException(ApiMessage.USER_NOT_FOUND));

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepo.save(user));
    }

    public void deleteUser(String id) {

        if (getUserById(id) == null) {
            throw new AppException(ApiMessage.USER_NOT_FOUND);
        }
        userRepo.deleteById(id);

    }
}
