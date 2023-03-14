package com.woowacourse.zzimkkong.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolationException;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.FORMAT_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.SERVER_ERROR_MESSAGE;

@Getter
@NoArgsConstructor
public class ErrorResponse {
    private String message;

    protected ErrorResponse(final String message) {
        this.message = message;
    }

    public static ErrorResponse from(final RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    public static ErrorResponse from(final ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().iterator().next().getMessage();
        return new ErrorResponse(message);
    }

    public static ErrorResponse invalidFormat() {
        return new ErrorResponse(FORMAT_MESSAGE);
    }

    public static ErrorResponse internalServerError() {
        return new ErrorResponse(SERVER_ERROR_MESSAGE);
    }
}
