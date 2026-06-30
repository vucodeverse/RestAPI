package com.phongvu.restapi.controller;

import com.phongvu.restapi.constraint.ErrorCode;
import com.phongvu.restapi.dto.request.TwoFactorVerificationRequest;
import com.phongvu.restapi.dto.response.ApiResponse;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.repository.UserRepository;
import com.phongvu.restapi.service.TwoFactorService;
import com.phongvu.restapi.service.impl.AuthenticationServiceImpl;
import com.phongvu.restapi.utils.exception.AppException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/identity/2fa")
@RequiredArgsConstructor
@Tag(name = "Two Factor Authentication")
public class TwoFactorController {

    private final TwoFactorService twoFactorService;
    private final UserRepository userRepository;
    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("/setup")
    public ResponseEntity<ApiResponse<Map<String, String>>> setup2fa() {
        String username = getUsernameFromContext();
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getTotpSecret() == null) {
            user.setTotpSecret(twoFactorService.generateNewSecret());
            userRepository.save(user);
        }

        String qrCode = twoFactorService.generateQrCodeImageUri(user.getTotpSecret(), user.getUsername());
        
        Map<String, String> response = new HashMap<>();
        response.put("qrCodeUri", qrCode);
        response.put("secret", user.getTotpSecret());

        return ResponseEntity.ok(ApiResponse.success(200, "2FA Setup", response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> verify2fa(@RequestBody TwoFactorVerificationRequest request) {
        String username = getUsernameFromContext();
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getTotpSecret() == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED); // or 2FA not set up
        }

        boolean isValid = twoFactorService.isOtpValid(user.getTotpSecret(), request.getOtp());
        if (!isValid) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Enable it if it's not enabled yet
        if (!user.is2faEnabled()) {
            user.set2faEnabled(true);
            userRepository.save(user);
        }

        // Return the real token
        String token = authenticationService.genToken(user);
        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .mfaRequired(false)
                .build();

        return ResponseEntity.ok(ApiResponse.success(200, "2FA Verified", authResponse));
    }

    private String getUsernameFromContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt) {
            return ((Jwt) principal).getSubject();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
}
