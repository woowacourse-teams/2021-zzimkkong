package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class InputFieldException extends ZzimkkongException {
    protected static final String EMAIL = "email";
    protected static final String PASSWORD = "password";
    protected static final String RESERVATION_PASSWORD = "password";
    protected static final String START_DATE_TIME = "startDateTime";
    protected static final String END_DATE_TIME = "endDateTime";
    protected static final String RESERVATION_ID = "reservationId";
    protected static final String SETTING = "setting";
    protected static final String SPACE_ID = "spaceId";
    protected static final String DAY_OF_WEEK = "dayOfWeek";

    private final String field;

    public InputFieldException(String message, HttpStatus status, String field) {
        super(message, status);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
