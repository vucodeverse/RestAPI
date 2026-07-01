package com.phongvu.restapi.controller;

import com.phongvu.restapi.constraint.SuccessCode;
import com.phongvu.restapi.dto.request.LogoutRequest;
import com.phongvu.restapi.dto.request.RefreshTokenRequest;
import com.phongvu.restapi.dto.response.ApiResponse;
import com.phongvu.restapi.dto.request.AuthenticationRequest;
import com.phongvu.restapi.dto.request.ForgotPasswordRequest;
import com.phongvu.restapi.dto.request.GoogleLoginRequest;
import com.phongvu.restapi.dto.request.ResetPasswordRequest;
import com.phongvu.restapi.dto.request.IntrospectRequest;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;
import com.phongvu.restapi.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
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
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody @Valid AuthenticationRequest request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.AUTHENTICATED.getCode(),
                SuccessCode.AUTHENTICATED.getMsg(),
                authenticationService.authenticate(request, httpServletRequest)));
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
     * Logs out a user by invalidating the JWT token.
     *
     * @param request the logout request containing JWT token
     * @return HTTP 200 response on success
     */
    @PostMapping(path = "logout")
    ResponseEntity<ApiResponse<Void>> logout(@RequestBody @Valid LogoutRequest request) {
        authenticationService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.LOGOUT_SUCCESS.getCode(),
                SuccessCode.LOGOUT_SUCCESS.getMsg(),
                null));
    }

    /**
     * Refreshes an expired JWT token using the refresh token mechanism.
     *
     * @param request the refresh token request
     * @return new JWT token
     */
    @PostMapping(path = "refresh")
    ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(@RequestBody @Valid RefreshTokenRequest request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.TOKEN_REFRESHED.getCode(),
                SuccessCode.TOKEN_REFRESHED.getMsg(),
                authenticationService.refreshToken(request, httpServletRequest)));
    }

    /**
     * Initiates password reset by sending a reset link to the user's email.
     *
     * @param request the forgot password request containing user email
     * @return HTTP 200 response (always, to prevent email enumeration)
     */
    @PostMapping(path = "forgot-password")
    ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.FORGOT_PASSWORD_SENT.getCode(),
                SuccessCode.FORGOT_PASSWORD_SENT.getMsg(),
                null));
    }

    /**
     * Resets a user's password using a valid reset token.
     *
     * @param request the reset password request containing token and new password
     * @return HTTP 200 response on success
     */
    @PostMapping(path = "reset-password")
    ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.PASSWORD_RESET_SUCCESS.getCode(),
                SuccessCode.PASSWORD_RESET_SUCCESS.getMsg(),
                null));
    }

    /**
     * Authenticates a user using their Google ID token.
     *
     * @param request the Google login request containing the Google ID token
     * @return JWT token on successful authentication
     */
    @PostMapping(path = "google-login")
    ResponseEntity<ApiResponse<AuthenticationResponse>> googleLogin(@RequestBody @Valid GoogleLoginRequest request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.GOOGLE_LOGIN_SUCCESS.getCode(),
                SuccessCode.GOOGLE_LOGIN_SUCCESS.getMsg(),
                authenticationService.googleLogin(request, httpServletRequest)));
    }
}
