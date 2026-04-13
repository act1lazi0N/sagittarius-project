package com.sagittarius.inventory.application.exception;

import com.sagittarius.common.exception.BaseErrorCode;

public enum InventoryErrorCode implements BaseErrorCode {
    PRODUCT_NOT_FOUND(404, "PRODUCT_NOT_FOUND", "Sản phẩm không tồn tại trong kho!"),
    OUT_OF_STOCK(400, "OUT_OF_STOCK", "Sản phẩm đã hết hàng hoặc không đủ số lượng!"),
    INVALID_QUANTITY(400, "INVALID_QUANTITY", "Số lượng nhập kho phải lớn hơn 0!"),
    JSON_PROCESS_ERROR(500, "JSON_ERROR", "Lỗi đóng gói dữ liệu sự kiện!");

    private final int status;
    private final String code;
    private final String message;

    InventoryErrorCode(int status, String code, String message) { this.status = status; this.code = code; this.message = message; }
    @Override public String getCode() { return code; }
    @Override public int getStatus() { return status; }
    @Override public String getMessage() { return message; }
}
