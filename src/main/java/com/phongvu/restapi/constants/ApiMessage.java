package com.phongvu.restapi.constants;

import org.springframework.http.HttpStatus;

public enum ApiMessage {
    USER_EXISTED(1001, HttpStatus.CONFLICT, "Username already exists"),
    USER_NOT_EXISTED(1002, HttpStatus.BAD_REQUEST, "Username does not exist"),
    USER_NOT_FOUND(1003, HttpStatus.NOT_FOUND, "User not found"),
    USER_CREATED(1004, HttpStatus.CREATED, "Create user successfully"),
    USER_UPDATED(1005, HttpStatus.OK, "Update user successfully"),
    USER_DELETED(1006, HttpStatus.NO_CONTENT, "Delete user successfully"),
    GET_USER_BY_ID(1007, HttpStatus.OK, "Get user successfully"),
    GET_ALL_USER(1008, HttpStatus.OK, "Get all users successfully"),
    UNAUTHENTICATED(2001, HttpStatus.UNAUTHORIZED, "Unauthenticated"),
    UNAUTHORIZED(2002, HttpStatus.FORBIDDEN, "You do not have permission"),
    INTERNAL_SERVER_ERROR(9999, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    ApiMessage(int code, HttpStatus httpStatus, String msg) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.msg = msg;
    }

    private int code;
    private HttpStatus httpStatus;
    private String msg;

    public String getMsg() {
        return msg;
    }
    public HttpStatus getHttpStatus() { return httpStatus; }
    public int getCode() {
        return code;
    }

}
