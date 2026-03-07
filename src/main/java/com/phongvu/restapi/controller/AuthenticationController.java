package com.phongvu.restapi.controller;

import com.phongvu.restapi.constants.SuccessCode;
import com.phongvu.restapi.dto.response.ApiResponse;
import com.phongvu.restapi.dto.request.AuthenticationRequest;
import com.phongvu.restapi.dto.request.IntrospectRequest;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;
import com.phongvu.restapi.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request the authentication request containing username and password
     * @return {@link ResponseEntity} containing {@link ApiResponse} with
     *         authentication result
     */
    @PostMapping
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody @Valid AuthenticationRequest request) {
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
    ResponseEntity<ApiResponse<IntrospectResponse>> introspect(
            @RequestBody @Valid IntrospectRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.INTROSPECT_SUCCESS.getCode(),
                SuccessCode.INTROSPECT_SUCCESS.getMsg(),
                authenticationService.introspect(request)));
    }
}
