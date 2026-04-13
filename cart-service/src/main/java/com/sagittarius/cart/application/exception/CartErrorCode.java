package com.sagittarius.cart.application.exception;

import com.sagittarius.common.exception.BaseErrorCode;

public enum CartErrorCode implements BaseErrorCode {
    CART_NOT_FOUND(404, "CART_NOT_FOUND", "Giỏ hàng không tồn tại hoặc đã bị xóa!"),
    ITEM_NOT_FOUND(404, "ITEM_NOT_FOUND", "Sản phẩm không có trong giỏ hàng!"),
    INVALID_QUANTITY(400, "INVALID_QUANTITY", "Số lượng sản phẩm phải lớn hơn 0!"),
    CART_EMPTY(400, "CART_EMPTY", "Giỏ hàng hiện đang trống!"),
    JSON_PROCESS_ERROR(500, "JSON_ERROR", "Lỗi xử lý dữ liệu giỏ hàng!");

    private final int status;
    private final String code;
    private final String message;

    CartErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override public String getCode() { return code; }
    @Override public int getStatus() { return status; }
    @Override public String getMessage() { return message; }
}