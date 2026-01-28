package com.phongvu.restapi.controller;

import com.nimbusds.jose.JOSEException;
import com.phongvu.restapi.dto.request.ApiResponse;
import com.phongvu.restapi.dto.request.AuthenticationRequest;
import com.phongvu.restapi.dto.request.IntrospectRequest;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;
import com.phongvu.restapi.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Authenticates a user and returns a JWT token.
     *
     * <p>This endpoint validates the provided username and password,
     * and returns a JWT token if authentication is successful.</p>
     *
     * @param request the authentication request containing username and password
     * @return {@link ResponseEntity} containing {@link ApiResponse} with authentication result
     */
    @PostMapping
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request) {
        var authenticationResult = authenticationService.authenticate(request);

        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
        response.setCode(200);
        response.setResult(authenticationResult);

        return ResponseEntity.ok(response);
    }

    /**
     * Introspect JWT token to check its validity.
     *
     * <p>This endpoint receives a JWT token from client,
     * delegates validation logic to {@link AuthenticationService},
     * and returns whether the token is valid or not.</p>
     *
     * @param request the introspection request containing JWT token
     * @return HTTP 200 response with token validity information
     */
    @PostMapping(path = "introspect")
    ResponseEntity<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request) {
        var result = authenticationService.introspect(request);

        ApiResponse<IntrospectResponse> response = new ApiResponse<>();
        response.setCode(200);
        response.setResult(result);

        return ResponseEntity.ok(response);
    }
}
