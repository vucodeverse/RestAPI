package com.phongvu.restapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSessionResponse {
    private String id;
    private String deviceInfo;
    private String ipAddress;
    private LocalDateTime createdAt;
}
