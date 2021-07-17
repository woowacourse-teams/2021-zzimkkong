package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;

import static com.woowacourse.zzimkkong.dto.Validator.FORMAT_MESSAGE;

public class ErrorResponse {
    private String message;
    private String field;

    public ErrorResponse() {

    }

    private ErrorResponse(final String message) {
        this(message, null);
    }

    private ErrorResponse(final String message, final String field) {
        this.message = message;
        this.field = field;
    }

    public static ErrorResponse of(final ZzimkkongException exception) {
        return new ErrorResponse(exception.getMessage(), exception.getField());
    }

    public static ErrorResponse of(final RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    public static ErrorResponse of(final MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        String field = exception.getFieldErrors().get(0).getField();
        return new ErrorResponse(message, field);
    }

    public static ErrorResponse of(final ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().iterator().next().getMessage();
        return new ErrorResponse(message);
    }

    public static ErrorResponse invalidFormat() {
        return new ErrorResponse(FORMAT_MESSAGE);
    }

    public String getMessage() {
        return message;
    }

    public String getField() {
        return field;
    }
}
