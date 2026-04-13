package com.sagittarius.payment.application.exception;

import com.sagittarius.common.exception.BaseErrorCode;

public enum PaymentErrorCode implements BaseErrorCode {
    WALLET_ALREADY_EXISTS(409, "WALLET_EXISTS", "Ví của khách hàng này đã tồn tại!"),
    JSON_PROCESS_ERROR(500, "JSON_ERROR", "Lỗi đóng gói dữ liệu sự kiện!");
    private final int status;
    private final String code;
    private final String message;

    PaymentErrorCode(int status, String code, String message) { this.status = status; this.code = code; this.message = message; }
    @Override public String getCode() { return code; }
    @Override public int getStatus() { return status; }
    @Override public String getMessage() { return message; }
}