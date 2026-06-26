package com.phongvu.restapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    private String email;
}
