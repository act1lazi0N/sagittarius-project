package com.sagittarius.common.web.handler;

import com.sagittarius.common.exception.BaseErrorCode;
import com.sagittarius.common.exception.BusinessException;
import com.sagittarius.common.web.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BaseGlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        BaseErrorCode errorCode = ex.getErrorCode();
        log.warn("Business Exception: {}", errorCode.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .type("https://api.sagittarius.com/errors/" + errorCode.getCode().toLowerCase())
                .title("Business Rule Violation")
                .status(errorCode.getStatus())
                .detail(errorCode.getMessage())
                .instance(request.getRequestURI())
                .errorCode(errorCode.getCode())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse response = ErrorResponse.builder()
                .type("https://api.sagittarius.com/errors/validation_failed")
                .title("Validation Failed")
                .status(400)
                .detail("Dữ liệu đầu vào không hợp lệ")
                .instance(request.getRequestURI())
                .errorCode("VALIDATION_ERROR")
                .timestamp(LocalDateTime.now())
                .validationErrors(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        log.error("Internal Server Error: ", ex);

        ErrorResponse response = ErrorResponse.builder()
                .type("https://api.sagittarius.com/errors/internal_error")
                .title("Internal Server Error")
                .status(500)
                .detail("Đã xảy ra lỗi hệ thống, vui lòng thử lại sau!")
                .instance(request.getRequestURI())
                .errorCode("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
