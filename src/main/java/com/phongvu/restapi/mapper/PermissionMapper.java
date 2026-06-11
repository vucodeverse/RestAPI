package com.phongvu.restapi.mapper;

import com.phongvu.restapi.dto.request.PermissionRequest;
import com.phongvu.restapi.dto.response.PermissionResponse;
import com.phongvu.restapi.model.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse (Permission permission);
}
