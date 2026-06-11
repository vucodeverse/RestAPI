package com.phongvu.restapi.controller;

import com.phongvu.restapi.constraint.SuccessCode;
import com.phongvu.restapi.dto.request.PermissionRequest;
import com.phongvu.restapi.dto.response.ApiResponse;
import com.phongvu.restapi.dto.response.PermissionResponse;
import com.phongvu.restapi.dto.response.UserResponse;
import com.phongvu.restapi.service.PermissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission")
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    ResponseEntity<ApiResponse<PermissionResponse>> create(@RequestBody PermissionRequest request) {
        return ResponseEntity
                .status(SuccessCode.PER_CREATED.getHttpStatus())
                .body(ApiResponse.success(
                        SuccessCode.PER_CREATED.getCode(),
                        SuccessCode.PER_CREATED.getMsg(),
                        permissionService.createPermission(request)));
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<PermissionResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.GET_ALL_PER.getCode(),
                SuccessCode.GET_ALL_PER.getMsg(),
                permissionService.getAllPermission()));
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
