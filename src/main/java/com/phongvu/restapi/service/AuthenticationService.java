package com.phongvu.restapi.service;

import com.phongvu.restapi.dto.request.*;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;
import com.phongvu.restapi.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpServletRequest);
    String genToken(User user, HttpServletRequest httpServletRequest);
    IntrospectResponse introspect(IntrospectRequest request);
    AuthenticationResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpServletRequest);
    void logout(LogoutRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    AuthenticationResponse googleLogin(com.phongvu.restapi.dto.request.GoogleLoginRequest request, HttpServletRequest httpServletRequest);
}
