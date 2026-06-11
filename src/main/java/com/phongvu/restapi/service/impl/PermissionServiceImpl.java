package com.phongvu.restapi.service.impl;

import com.phongvu.restapi.dto.request.PermissionRequest;
import com.phongvu.restapi.dto.response.PermissionResponse;
import com.phongvu.restapi.mapper.PermissionMapper;
import com.phongvu.restapi.model.Permission;
import com.phongvu.restapi.repository.PermissionRepo;
import com.phongvu.restapi.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepo permissionRepo;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepo.save(permission));
    }

    @Override
    public List<PermissionResponse> getAllPermission() {
        return permissionRepo.findAll().stream().map(permissionMapper::toPermissionResponse).toList();
    }

    @Override
    public void deletePermission(Integer id) {
        permissionRepo.deleteById(id);
    }


}
