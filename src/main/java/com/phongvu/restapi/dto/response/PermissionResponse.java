package com.phongvu.restapi.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PermissionResponse {
    private int id;
    private String name;
    private String description;
}
