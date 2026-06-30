package com.phongvu.restapi.service;

import com.phongvu.restapi.dto.request.*;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpServletRequest);
    IntrospectResponse introspect(IntrospectRequest request);
    AuthenticationResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpServletRequest);
    void logout(LogoutRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    String genToken(com.phongvu.restapi.model.User user, HttpServletRequest httpServletRequest);
}
