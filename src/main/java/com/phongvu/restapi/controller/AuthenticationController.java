package com.phongvu.restapi.controller;

import com.phongvu.restapi.constraint.SuccessCode;
import com.phongvu.restapi.dto.request.LogoutRequest;
import com.phongvu.restapi.dto.request.RefreshTokenRequest;
import com.phongvu.restapi.dto.response.ApiResponse;
import com.phongvu.restapi.dto.request.AuthenticationRequest;
import com.phongvu.restapi.dto.request.IntrospectRequest;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;
import com.phongvu.restapi.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/identity")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request the authentication request containing username and password
     * @return {@link ResponseEntity} containing {@link ApiResponse} with
     *         authentication result
     */
    @PostMapping("authenticate")
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.AUTHENTICATED.getCode(),
                SuccessCode.AUTHENTICATED.getMsg(),
                authenticationService.authenticate(request)));
    }

    /**
     * Introspect JWT token to check its validity.
     *
     * @param request the introspection request containing JWT token
     * @return HTTP 200 response with token validity information
     */
    @PostMapping(path = "introspect")
    ResponseEntity<ApiResponse<IntrospectResponse>> introspect(@RequestBody @Valid IntrospectRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.INTROSPECT_SUCCESS.getCode(),
                SuccessCode.INTROSPECT_SUCCESS.getMsg(),
                authenticationService.introspect(request)));
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping(path = "logout")
    ResponseEntity<ApiResponse<Void>> logout(@RequestBody @Valid LogoutRequest request) {
        authenticationService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(200, "Logout successful", null));
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping(path = "refresh")
    ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                200,
                "Token refreshed successfully",
                authenticationService.refreshToken(request)));
    }
}
