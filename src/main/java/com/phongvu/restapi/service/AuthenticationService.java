package com.phongvu.restapi.service;

import com.phongvu.restapi.dto.request.AuthenticationRequest;
import com.phongvu.restapi.dto.request.IntrospectRequest;
import com.phongvu.restapi.dto.request.LogoutRequest;
import com.phongvu.restapi.dto.request.RefreshTokenRequest;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    IntrospectResponse introspect(IntrospectRequest request);
    AuthenticationResponse refreshToken(RefreshTokenRequest request);
    void logout(LogoutRequest request);
}
