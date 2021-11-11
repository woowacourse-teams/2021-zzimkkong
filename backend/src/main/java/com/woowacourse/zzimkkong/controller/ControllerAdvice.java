package com.woowacourse.zzimkkong.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.woowacourse.zzimkkong.dto.ErrorResponse;
import com.woowacourse.zzimkkong.dto.InputFieldErrorResponse;
import com.woowacourse.zzimkkong.dto.OAuthLoginFailErrorResponse;
import com.woowacourse.zzimkkong.exception.InputFieldException;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import com.woowacourse.zzimkkong.exception.infrastructure.InfrastructureMalfunctionException;
import com.woowacourse.zzimkkong.exception.member.NoSuchOAuthMemberException;
import com.woowacourse.zzimkkong.infrastructure.transaction.TransactionThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.FORMAT_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.SERVER_ERROR_MESSAGE;
import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {
    private static final String MESSAGE_FORMAT = "{} (transactionId: {})";

    private final TransactionThreadLocal transactionThreadLocal;

    public ControllerAdvice(TransactionThreadLocal transactionThreadLocal) {
        this.transactionThreadLocal = transactionThreadLocal;
    }

    @ExceptionHandler(NoSuchOAuthMemberException.class)
    public ResponseEntity<OAuthLoginFailErrorResponse> oAuthLoginFailHandler(final NoSuchOAuthMemberException exception) {
        logInfo(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(OAuthLoginFailErrorResponse.from(exception));
    }

    @ExceptionHandler(InputFieldException.class)
    public ResponseEntity<InputFieldErrorResponse> inputFieldExceptionHandler(final InputFieldException exception) {
        logInfo(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(InputFieldErrorResponse.from(exception));
    }

    @ExceptionHandler(InfrastructureMalfunctionException.class)
    public ResponseEntity<ErrorResponse> wrongConfigurationOfInfrastructureException(final InfrastructureMalfunctionException exception) {
        logWarn(exception.getMessage(), exception);
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(ZzimkkongException.class)
    public ResponseEntity<ErrorResponse> zzimkkongExceptionHandler(final ZzimkkongException exception) {
        logInfo(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<InputFieldErrorResponse> invalidArgumentHandler(final MethodArgumentNotValidException exception) {
        logInfo(exception.getMessage());
        return ResponseEntity.badRequest().body(InputFieldErrorResponse.from(exception));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> invalidParamHandler(final ConstraintViolationException exception) {
        logInfo(exception.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.from(exception));
    }

    @ExceptionHandler({InvalidFormatException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> invalidFormatHandler() {
        logInfo(FORMAT_MESSAGE);
        return ResponseEntity.badRequest().body(ErrorResponse.invalidFormat());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Void> invalidDataAccessHandler(final DataAccessException exception) {
        logWarn(SERVER_ERROR_MESSAGE, exception);
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> unhandledExceptionHandler(final Exception exception) {
        logWarn(exception.getMessage(), exception);
        return ResponseEntity.internalServerError().build();
    }

    private void logInfo(String message) {
        log.info(MESSAGE_FORMAT,
                message,
                value("transaction", transactionThreadLocal.getTransactionId()));
    }

    private void logWarn(String message, Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        exception.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();

        log.warn(MESSAGE_FORMAT,
                message,
                value("transaction", transactionThreadLocal.getTransactionId()),
                value("stack_trace", stackTrace));
    }

}
