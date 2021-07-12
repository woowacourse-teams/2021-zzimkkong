package com.woowacourse.zzimkkong.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.woowacourse.zzimkkong.dto.ErrorResponse;
import com.woowacourse.zzimkkong.exception.MapException;
import com.woowacourse.zzimkkong.exception.MemberException;
import com.woowacourse.zzimkkong.exception.ReservationException;
import com.woowacourse.zzimkkong.exception.SpaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

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
        logger.warn("예상치 못한 문제가 발생했습니다. 개발자에게 문의하세요.");
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> invalidFormatHandler() {
        logger.warn("날짜 및 시간 데이터 형식이 올바르지 않습니다.");
        return ResponseEntity.badRequest().body(ErrorResponse.invalidFormat());
    }
}
