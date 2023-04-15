package com.woowacourse.zzimkkong.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.SERVER_ERROR_MESSAGE;
import static com.woowacourse.zzimkkong.infrastructure.message.MessageUtils.LINE_SEPARATOR;

@Getter
public class ZzimkkongException extends RuntimeException {
    private final HttpStatus status;

    public ZzimkkongException() {
        super(SERVER_ERROR_MESSAGE);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ZzimkkongException(final HttpStatus status) {
        super(SERVER_ERROR_MESSAGE);
        this.status = status;
    }

    public ZzimkkongException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ZzimkkongException(final String message, final Throwable cause, final HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
