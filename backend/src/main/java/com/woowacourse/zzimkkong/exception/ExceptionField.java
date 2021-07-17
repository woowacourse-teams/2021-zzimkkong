package com.woowacourse.zzimkkong.exception;

public enum ExceptionField {
    EMAIL("email"),
    PASSWORD("password"),
    START_DATETIME("startDateTime"),
    END_DATETIME("endDateTime"),
    TOKEN("accessToken"),
    MAP_ID("mapId"),
    SPACE_ID("spaceId"),
    RESERVATION_ID("reservationId");

    private final String fieldName;

    ExceptionField(final String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
