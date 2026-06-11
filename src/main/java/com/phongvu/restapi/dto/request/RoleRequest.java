package com.phongvu.restapi.dto.request;

import lombok.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoleRequest {
    private String name;
    private String description;
    private Set<Integer> permissionIds;
}
