package com.phongvu.restapi.controller;

import com.phongvu.restapi.constraint.SuccessCode;
import com.phongvu.restapi.dto.response.ApiResponse;
import com.phongvu.restapi.dto.request.UserCreationRequest;
import com.phongvu.restapi.dto.request.UserUpdateRequest;
import com.phongvu.restapi.dto.response.UserResponse;
import com.phongvu.restapi.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "User")
public class  UserController {

    private final IUserService userService;

    @PostMapping("create")
    ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ResponseEntity
                .status(SuccessCode.USER_CREATED.getHttpStatus())
                .body(ApiResponse.success(
                        SuccessCode.USER_CREATED.getCode(),
                        SuccessCode.USER_CREATED.getMsg(),
                        userService.createUser(request)));
    }

    @GetMapping("getAll")
    ResponseEntity<ApiResponse<List<UserResponse>>> getAllUser() {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.GET_ALL_USER.getCode(),
                SuccessCode.GET_ALL_USER.getMsg(),
                userService.getAllUser()));
    }

    @GetMapping("getPage")
    ResponseEntity<ApiResponse<Page<UserResponse>>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.GET_ALL_USER.getCode(),
                SuccessCode.GET_ALL_USER.getMsg(),
                userService.getPage(page, size)));
    }

    @GetMapping("/getDetail/{id}")
    ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.GET_USER_BY_ID.getCode(),
                SuccessCode.GET_USER_BY_ID.getMsg(),
                userService.getUserById(id)));
    }

    @GetMapping("/profile")
    ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.GET_PROFILE.getCode(),
                SuccessCode.GET_PROFILE.getMsg(),
                userService.getProfile()));
    }

    @PutMapping("update/{id}")
    ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @RequestBody @Valid UserUpdateRequest request,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.USER_UPDATED.getCode(),
                SuccessCode.USER_UPDATED.getMsg(),
                userService.updateUser(id, request)));
    }

    @DeleteMapping("delete/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
