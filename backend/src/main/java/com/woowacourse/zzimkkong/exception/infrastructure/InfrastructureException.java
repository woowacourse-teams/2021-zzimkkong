package com.woowacourse.zzimkkong.exception.infrastructure;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InfrastructureException extends ZzimkkongException {
    public static final String MAP_IMAGE_SVG = "mapImageSvg";
    public static final String PUBLIC_MAP_ID = "publicMapId";
    public static final String SECRET_KEY = "secretKey";

    public InfrastructureException(String message, HttpStatus httpStatus, String field) {
        super(message, httpStatus, field);
    }

    public InfrastructureException(String message, Throwable cause, HttpStatus status, String field) {
        super(message, cause, status, field);
    }
}
