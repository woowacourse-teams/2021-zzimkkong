package com.woowacourse.zzimkkong.dto;

import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;

import static com.woowacourse.zzimkkong.dto.Validator.FORMAT_MESSAGE;

public class ErrorResponse {
    private String message;

    public ErrorResponse() {

    }

    private ErrorResponse(final String message) {
        this.message = message;
    }

    public static ErrorResponse from(final RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    public static ErrorResponse from(final MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ErrorResponse(message);
    }

    public static ErrorResponse from(final ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().iterator().next().getMessage();
        return new ErrorResponse(message);
    }

    public static ErrorResponse invalidFormat() {
        return new ErrorResponse(FORMAT_MESSAGE);
    }

    public String getMessage() {
        return message;
    }
}
