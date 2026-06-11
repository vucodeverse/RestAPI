package com.phongvu.restapi.service;

import com.phongvu.restapi.dto.request.PermissionRequest;
import com.phongvu.restapi.dto.response.PermissionResponse;

import java.util.List;


public interface PermissionService {
    PermissionResponse createPermission(PermissionRequest request);
    List<PermissionResponse> getAllPermission();
    void deletePermission(Integer id);
}
