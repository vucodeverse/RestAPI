package com.phongvu.restapi.mapper;

import com.phongvu.restapi.dto.request.RoleRequest;
import com.phongvu.restapi.dto.response.RoleResponse;
import com.phongvu.restapi.model.Role;
import org.mapstruct.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;

//@Mapper(componentModel = "spring")
public interface RoleMapper extends JpaRepository<Role, Integer> {
    RoleResponse toRoleResponse (Role role);
    Role toRole (RoleRequest request);
}
