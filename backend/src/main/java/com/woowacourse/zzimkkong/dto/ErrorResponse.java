package com.woowacourse.zzimkkong.dto;

import javax.validation.ConstraintViolationException;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.FORMAT_MESSAGE;

public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public static ErrorResponse from(RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
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
