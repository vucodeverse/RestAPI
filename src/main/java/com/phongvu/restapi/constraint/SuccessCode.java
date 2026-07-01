package com.phongvu.restapi.constraint;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {
    // User operations
    USER_CREATED(1004, HttpStatus.CREATED, "Create user successfully"),
    USER_UPDATED(1005, HttpStatus.OK, "Update user successfully"),
    USER_DELETED(1006, HttpStatus.NO_CONTENT, "Delete user successfully"),
    GET_USER_BY_ID(1007, HttpStatus.OK, "Get user successfully"),
    GET_ALL_USER(1008, HttpStatus.OK, "Get all users successfully"),
    GET_PROFILE(1009, HttpStatus.OK, "Get profile successfully"),

    // Permission operations
    PER_CREATED(1014, HttpStatus.CREATED, "Permission created successfully"),
    GET_ALL_PER(1015, HttpStatus.OK, "Get all permissions successfully"),

    // Authentication operations
    AUTHENTICATED(2003, HttpStatus.OK, "Authenticated successfully"),
    INTROSPECT_SUCCESS(2004, HttpStatus.OK, "Token introspection successful"),
    LOGOUT_SUCCESS(2005, HttpStatus.OK, "Logout successful"),
    TOKEN_REFRESHED(2006, HttpStatus.OK, "Token refreshed successfully"),
    FORGOT_PASSWORD_SENT(2007, HttpStatus.OK, "If email exists, a reset link will be sent"),
    PASSWORD_RESET_SUCCESS(2008, HttpStatus.OK, "Password has been reset successfully"),
    GOOGLE_LOGIN_SUCCESS(2009, HttpStatus.OK, "Google login successfully"),

    // Session operations
    GET_SESSIONS_SUCCESS(3001, HttpStatus.OK, "Sessions retrieved successfully"),
    SESSION_REVOKED(3002, HttpStatus.OK, "Session revoked successfully"),

    // 2FA operations
    TOTP_SETUP_SUCCESS(4001, HttpStatus.OK, "2FA setup successful"),
    TOTP_VERIFY_SUCCESS(4002, HttpStatus.OK, "2FA verification successful"),
    TOTP_DISABLED(4003, HttpStatus.OK, "2FA disabled successfully");

    SuccessCode(int code, HttpStatus httpStatus, String msg) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.msg = msg;
    }

    private final int code;
    private final HttpStatus httpStatus;
    private final String msg;

}
