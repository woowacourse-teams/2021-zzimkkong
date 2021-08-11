package com.woowacourse.zzimkkong.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InputFieldException extends ZzimkkongException {
    protected static final String EMAIL = "email";
    protected static final String PASSWORD = "password";
    protected static final String RESERVATION_PASSWORD = "password";
    protected static final String START_DATE_TIME = "startDateTime";
    protected static final String END_DATE_TIME = "endDateTime";

    private final String field;

    public InputFieldException(final String message, final HttpStatus status, final String field) {
        super(message, status);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
