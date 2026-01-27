package com.phongvu.restapi.controller;

import com.phongvu.restapi.dto.request.ApiResponse;
import com.phongvu.restapi.dto.request.AuthenticationRequest;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.service.UserService;
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

    private final UserService userService;

    @PostMapping
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request) {
        boolean result = userService.authenticate(request);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(result);
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
        response.setResult(authenticationResponse);
        return ResponseEntity.ok(response);
    }
}
