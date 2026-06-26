package com.phongvu.restapi.service;

import com.phongvu.restapi.dto.request.*;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    IntrospectResponse introspect(IntrospectRequest request);
    AuthenticationResponse refreshToken(RefreshTokenRequest request);
    void logout(LogoutRequest request);
    void forgotPassword(ForgotPasswordRequest request);
}
