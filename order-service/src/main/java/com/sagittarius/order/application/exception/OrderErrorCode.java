package com.sagittarius.order.application.exception;

import com.sagittarius.common.exception.BaseErrorCode;

public enum OrderErrorCode implements BaseErrorCode {
    ORDER_NOT_FOUND(404, "ORDER_NOT_FOUND", "Không tìm thấy đơn hàng!"),
    CANNOT_CANCEL_SHIPPED_ORDER(400, "INVALID_STATE", "Không thể hủy đơn hàng đã được giao!"),
    PRODUCT_PRICE_UNAVAILABLE(400, "PRICE_ERROR", "Không thể lấy giá sản phẩm từ hệ thống!"),
    JSON_PROCESS_ERROR(500, "JSON_ERROR", "Lỗi đóng gói dữ liệu sự kiện!");

    private final int status;
    private final String code;
    private final String message;

    OrderErrorCode(int status, String code, String message) { this.status = status; this.code = code; this.message = message; }
    @Override public String getCode() { return code; }
    @Override public int getStatus() { return status; }
    @Override public String getMessage() { return message; }
}
