package com.phongvu.restapi.constants;

public enum ApiMessage {
    USER_EXITED(400, "UserName is exited!!"),
    USER_NOT_EXITED(400, "UserName is not exited!!"),
    USER_NOT_FOUND(400, "User not found!!"),
    USER_CREATED(201, "Create user successfully"),
    USER_UPDATED(200, "Update user successfully"),
    USER_DELETED(204, "Delete user successfully"),
    GET_USER_BY_ID(200, "Get user successfully"),
    GET_ALL_USER(200, "Get all users successfully")
    ;

    ApiMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

}
