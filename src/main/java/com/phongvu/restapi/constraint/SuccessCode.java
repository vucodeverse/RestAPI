package com.phongvu.restapi.constraint;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {
    USER_CREATED(1004, HttpStatus.CREATED, "Create user successfully"),
    USER_UPDATED(1005, HttpStatus.OK, "Update user successfully"),
    USER_DELETED(1006, HttpStatus.NO_CONTENT, "Delete user successfully"),
    GET_USER_BY_ID(1007, HttpStatus.OK, "Get user successfully"),
    GET_ALL_USER(1008, HttpStatus.OK, "Get all users successfully"),
    GET_PROFILE(1009, HttpStatus.OK, "Get profile successfully"),
    PER_CREATED(1014, HttpStatus.CREATED, "Permission user successfully"),
    GET_ALL_PER(1008, HttpStatus.OK, "Get all permissions successfully"),
    AUTHENTICATED(2003, HttpStatus.OK, "Authenticated successfully"),
    INTROSPECT_SUCCESS(2004, HttpStatus.OK, "Token introspection successful");

    SuccessCode(int code, HttpStatus httpStatus, String msg) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.msg = msg;
    }

    private final int code;
    private final HttpStatus httpStatus;
    private final String msg;

}
