package com.woowacourse.zzimkkong.dto;

import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;

public class ErrorResponse {
    private String message;

    public ErrorResponse() {

    }

    private ErrorResponse(final String message) {
        this.message = message;
    }

    public static ErrorResponse of(final RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    public static ErrorResponse of(final MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ErrorResponse(message);
    }

    public static ErrorResponse of(final ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().iterator().next().getMessage();
        return new ErrorResponse(message);
    }

    public static ErrorResponse invalidFormat() {
        return new ErrorResponse("날짜 및 시간 데이터 형식이 올바르지 않습니다.");
    }

    public String getMessage() {
        return message;
    }
}
