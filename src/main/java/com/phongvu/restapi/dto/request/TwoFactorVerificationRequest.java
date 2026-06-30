package com.phongvu.restapi.dto.request;

import lombok.Data;

@Data
public class TwoFactorVerificationRequest {
    private String otp;
}
