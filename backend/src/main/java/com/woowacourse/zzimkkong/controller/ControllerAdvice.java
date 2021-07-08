package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.ErrorResponse;
import com.woowacourse.zzimkkong.exception.MemberException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.Constraint;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> memberExceptionHandler(final MemberException exception) {
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
}
