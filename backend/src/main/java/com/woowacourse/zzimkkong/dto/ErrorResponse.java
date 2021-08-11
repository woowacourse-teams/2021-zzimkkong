package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import lombok.Getter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.FORMAT_MESSAGE;

@Getter
public class ErrorResponse {
    private final String message;

    public ErrorResponse(final String message) {
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
}
