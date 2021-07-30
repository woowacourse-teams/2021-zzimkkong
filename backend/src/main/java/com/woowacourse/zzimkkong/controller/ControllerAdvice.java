package com.woowacourse.zzimkkong.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.woowacourse.zzimkkong.dto.ErrorResponse;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import com.woowacourse.zzimkkong.exception.authorization.AuthorizationException;
import com.woowacourse.zzimkkong.exception.infrastructure.S3UploadException;
import com.woowacourse.zzimkkong.exception.infrastructure.SvgToPngConvertException;
import com.woowacourse.zzimkkong.exception.map.MapException;
import com.woowacourse.zzimkkong.exception.member.MemberException;
import com.woowacourse.zzimkkong.exception.reservation.ReservationException;
import com.woowacourse.zzimkkong.exception.space.SpaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

import static com.woowacourse.zzimkkong.dto.Validator.FORMAT_MESSAGE;
import static com.woowacourse.zzimkkong.dto.Validator.SERVER_ERROR_MESSAGE;

@RestControllerAdvice
public class ControllerAdvice {
    private final Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);

    @ExceptionHandler({MemberException.class, MapException.class, SpaceException.class, ReservationException.class, AuthorizationException.class})
    public ResponseEntity<ErrorResponse> zzimkkongExceptionHandler(final ZzimkkongException exception) {
        logger.info(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidArgumentHandler(final MethodArgumentNotValidException exception) {
        logger.info(exception.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> invalidParamHandler(final ConstraintViolationException exception) {
        logger.info(exception.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> invalidFormatHandler() {
        logger.info(FORMAT_MESSAGE);
        return ResponseEntity.badRequest().body(ErrorResponse.invalidFormat());
    }

    @ExceptionHandler(SvgToPngConvertException.class)
    public ResponseEntity<ErrorResponse> invalidSvgToConvertHandler(final SvgToPngConvertException exception) {
        logger.warn(exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(S3UploadException.class)
    public ResponseEntity<ErrorResponse> uploadFailureHandler(final S3UploadException exception) {
        logger.warn(exception.getMessage(), exception);
        return ResponseEntity.internalServerError().body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> invalidDataAccessHandler(final DataAccessException exception) {
        logger.warn(SERVER_ERROR_MESSAGE, exception);
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandledExceptionHandler(final Exception exception) {
        logger.warn(exception.getMessage(), exception);
        return ResponseEntity.internalServerError().build();
    }
}
