package com.sagittarius.common.exception;

public interface BaseErrorCode {
    String getCode();
    int getStatus();
    String getMessage();
}