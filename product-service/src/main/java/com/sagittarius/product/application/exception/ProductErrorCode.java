package com.sagittarius.product.application.exception;

import com.sagittarius.common.exception.BaseErrorCode;

public enum ProductErrorCode implements BaseErrorCode {
    PRODUCT_NOT_FOUND(404, "PRODUCT_NOT_FOUND", "Không tìm thấy sản phẩm với mã SKU cung cấp!");

    private final int status;
    private final String code;
    private final String message;

    ProductErrorCode(int status, String code, String message) { this.status = status; this.code = code; this.message = message; }
    @Override public String getCode() { return code; }
    @Override public int getStatus() { return status; }
    @Override public String getMessage() { return message; }
}