package com.woowacourse.zzimkkong.dto;

import org.springframework.web.bind.MethodArgumentNotValidException;

public class ErrorResponse {
    private final String message;

    private ErrorResponse(final String message) {
        this.message = message;
    }

    public static ErrorResponse of(RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    public static ErrorResponse of(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ErrorResponse(message);
    }

    public String getMessage() {
        return message;
    }
}
