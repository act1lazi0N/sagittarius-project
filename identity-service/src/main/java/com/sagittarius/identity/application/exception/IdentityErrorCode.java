package com.sagittarius.identity.application.exception;

import com.sagittarius.common.exception.BaseErrorCode;

public enum IdentityErrorCode implements BaseErrorCode {
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "Không tìm thấy người dùng!"),
    USERNAME_ALREADY_EXISTS(400, "USERNAME_EXISTS", "Tên đăng nhập đã được sử dụng!"),
    EMAIL_ALREADY_EXISTS(400, "EMAIL_EXISTS", "Email đã tồn tại!"),
    KEYCLOAK_REGISTER_FAILED(500, "KEYCLOAK_ERROR", "Lỗi tạo tài khoản trên hệ thống xác thực!");

    private final int status;
    private final String code;
    private final String message;

    IdentityErrorCode(int status, String code, String message) { this.status = status; this.code = code; this.message = message; }
    @Override public String getCode() { return code; }
    @Override public int getStatus() { return status; }
    @Override public String getMessage() { return message; }
}