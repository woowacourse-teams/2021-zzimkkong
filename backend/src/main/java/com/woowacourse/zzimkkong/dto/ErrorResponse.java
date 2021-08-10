package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.FORMAT_MESSAGE;

@Getter
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private String field;

    private ErrorResponse(final String message) {
        this(message, null);
    }

    private ErrorResponse(final String message, final String field) {
        this.message = message;
        this.field = field;
    }

    public static ErrorResponse from(final ZzimkkongException exception) {
        return new ErrorResponse(exception.getMessage(), exception.getField());
    }

    public static ErrorResponse from(final RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    public static ErrorResponse from(final MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        String field = exception.getFieldErrors().get(0).getField();
        return new ErrorResponse(message, field);
    }

    public static ErrorResponse from(final ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().iterator().next().getMessage();
        return new ErrorResponse(message);
    }

    public static ErrorResponse invalidFormat() {
        return new ErrorResponse(FORMAT_MESSAGE);
    }
}
