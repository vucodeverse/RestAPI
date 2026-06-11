package com.phongvu.restapi.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PermissionRequest {
    private String name;
    private String description;
}
