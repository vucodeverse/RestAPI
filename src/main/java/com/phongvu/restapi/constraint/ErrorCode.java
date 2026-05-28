package com.phongvu.restapi.constraint;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_EXISTED(1001, HttpStatus.CONFLICT, "Username already exists"),
    USER_NOT_EXISTED(1002, HttpStatus.BAD_REQUEST, "Username does not exist"),
    USER_NOT_FOUND(1003, HttpStatus.NOT_FOUND, "User not found"),
    UNAUTHENTICATED(2001, HttpStatus.UNAUTHORIZED, "Unauthenticated"),
    UNAUTHORIZED(2002, HttpStatus.FORBIDDEN, "You do not have permission"),
    INTERNAL_SERVER_ERROR(9999, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    ErrorCode(int code, HttpStatus httpStatus, String msg) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.msg = msg;
    }

    private final int code;
    private final HttpStatus httpStatus;
    private final String msg;

}
