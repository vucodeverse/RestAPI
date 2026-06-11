package com.phongvu.restapi.dto.response;

import lombok.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoleResponse {
    private Integer id;
    private String name;
    private String description;
    private Set<PermissionResponse> permissions;
}
