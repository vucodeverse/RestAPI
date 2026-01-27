package com.phongvu.restapi.controller;

import com.phongvu.restapi.constants.ApiMessage;
import com.phongvu.restapi.dto.request.ApiResponse;
import com.phongvu.restapi.dto.request.UserCreationRequest;
import com.phongvu.restapi.dto.request.UserUpdateRequest;
import com.phongvu.restapi.dto.response.UserResponse;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    ResponseEntity<ApiResponse<User>> createUser(
            @RequestBody @Valid UserCreationRequest request) {

        ApiResponse<User> response = new ApiResponse<>();
        response.setCode(ApiMessage.USER_CREATED.getCode());
        response.setMessage(ApiMessage.USER_CREATED.getMsg());
        response.setResult(userService.createRequestUser(request));

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<User>>> getAllUser() {

        ApiResponse<List<User>> response = new ApiResponse<>();
        response.setCode(ApiMessage.GET_ALL_USER.getCode());
        response.setMessage(ApiMessage.GET_ALL_USER.getMsg());
        response.setResult(userService.getAllUser());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String id) {

        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("Get user successfully");
        response.setResult(userService.getUserById(id));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @RequestBody @Valid UserUpdateRequest request,
            @PathVariable String id) {

        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("Update user successfully");
        response.setResult(userService.updateUser(id, request));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> removeUser(@PathVariable String id) {

        userService.deleteUser(id);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(204);
        response.setMessage("Delete successfully");

        //return ResponseEntity.status(204).body(response);
        return ResponseEntity.ok(response);
    }

}
