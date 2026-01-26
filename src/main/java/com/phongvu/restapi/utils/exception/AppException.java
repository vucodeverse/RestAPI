package com.phongvu.restapi.utils.exception;


import com.phongvu.restapi.constants.ApiMessage;

public class AppException extends RuntimeException {
    private final ApiMessage errorCode;

    public AppException(ApiMessage errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    public ApiMessage getErrorCode() {
        return errorCode;
    }
}
