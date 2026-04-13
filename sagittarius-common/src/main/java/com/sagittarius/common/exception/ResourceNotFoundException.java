package com.sagittarius.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final BaseErrorCode errorCode;

    public ResourceNotFoundException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
