package com.woowacourse.zzimkkong.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.woowacourse.zzimkkong.dto.ErrorResponse;
import com.woowacourse.zzimkkong.exception.MapException;
import com.woowacourse.zzimkkong.exception.MemberException;
import com.woowacourse.zzimkkong.exception.ReservationException;
import com.woowacourse.zzimkkong.exception.SpaceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> memberExceptionHandler(final MemberException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(MapException.class)
    public ResponseEntity<ErrorResponse> mapExceptionHandler(final MapException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(SpaceException.class)
    public ResponseEntity<ErrorResponse> spaceExceptionHandler(final SpaceException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<ErrorResponse> reservationExceptionHandler(final ReservationException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidArgumentHandler(final MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> invalidParamHandler(final ConstraintViolationException exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> invalidDataAccessHandler() {
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> invalidFormatHandler() {
        return ResponseEntity.badRequest().body(ErrorResponse.invalidFormat());
    }
}
