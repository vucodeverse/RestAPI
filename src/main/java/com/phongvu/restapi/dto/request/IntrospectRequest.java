package com.phongvu.restapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IntrospectRequest {
    @NotBlank(message = "Token is required")
    private String token;
}
