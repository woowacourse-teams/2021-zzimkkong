package com.woowacourse.zzimkkong.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.woowacourse.zzimkkong.dto.ErrorResponse;
import com.woowacourse.zzimkkong.dto.InputFieldErrorResponse;
import com.woowacourse.zzimkkong.dto.OAuthLoginFailErrorResponse;
import com.woowacourse.zzimkkong.exception.InputFieldException;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import com.woowacourse.zzimkkong.exception.infrastructure.InfrastructureMalfunctionException;
import com.woowacourse.zzimkkong.exception.member.NoSuchOAuthMemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.FORMAT_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.SERVER_ERROR_MESSAGE;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(NoSuchOAuthMemberException.class)
    public ResponseEntity<OAuthLoginFailErrorResponse> oAuthLoginFailHandler(final NoSuchOAuthMemberException exception) {
        log.info(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(OAuthLoginFailErrorResponse.from(exception));
    }

    @ExceptionHandler(InputFieldException.class)
    public ResponseEntity<InputFieldErrorResponse> inputFieldExceptionHandler(final InputFieldException exception) {
        log.info(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(InputFieldErrorResponse.from(exception));
    }

    @ExceptionHandler(InfrastructureMalfunctionException.class)
    public ResponseEntity<ErrorResponse> wrongConfigurationOfInfrastructureException(final InfrastructureMalfunctionException exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(ZzimkkongException.class)
    public ResponseEntity<ErrorResponse> zzimkkongExceptionHandler(final ZzimkkongException exception) {
        log.info(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<InputFieldErrorResponse> invalidArgumentHandler(final MethodArgumentNotValidException exception) {
        log.info(exception.getMessage());
        return ResponseEntity.badRequest().body(InputFieldErrorResponse.from(exception));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> invalidParamHandler(final ConstraintViolationException exception) {
        log.info(exception.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.from(exception));
    }

    @ExceptionHandler({InvalidFormatException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> invalidFormatHandler() {
        log.info(FORMAT_MESSAGE);
        return ResponseEntity.badRequest().body(ErrorResponse.invalidFormat());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Void> invalidDataAccessHandler(final DataAccessException exception) {
        log.warn(SERVER_ERROR_MESSAGE, exception);
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> unhandledExceptionHandler(final Exception exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity.internalServerError().build();
    }
}
