package com.woowacourse.zzimkkong.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.woowacourse.zzimkkong.dto.ErrorResponse;
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

import static com.woowacourse.zzimkkong.dto.Validator.*;

@RestControllerAdvice
public class ControllerAdvice {
    private final Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> memberExceptionHandler(final MemberException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(MapException.class)
    public ResponseEntity<ErrorResponse> mapExceptionHandler(final MapException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(SpaceException.class)
    public ResponseEntity<ErrorResponse> spaceExceptionHandler(final SpaceException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<ErrorResponse> reservationExceptionHandler(final ReservationException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidArgumentHandler(final MethodArgumentNotValidException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> invalidParamHandler(final ConstraintViolationException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> invalidDataAccessHandler() {
        logger.warn(SERVER_ERROR_MESSAGE);
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> invalidFormatHandler() {
        logger.warn(FORMAT_MESSAGE);
        return ResponseEntity.badRequest().body(ErrorResponse.invalidFormat());
    }
}
