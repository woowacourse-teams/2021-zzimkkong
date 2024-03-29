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
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;
import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice implements RequestRejectedHandler {
    private static final String MESSAGE_FORMAT = "%s (traceId: %s)";
    private static final String TRACE_ID_KEY = "traceId";

    @ExceptionHandler(NoSuchOAuthMemberException.class)
    public ResponseEntity<OAuthLoginFailErrorResponse> oAuthLoginFailHandler(final NoSuchOAuthMemberException exception) {
        logInfo(exception);
        return ResponseEntity
                .status(exception.getStatus())
                .body(OAuthLoginFailErrorResponse.from(exception));
    }

    @ExceptionHandler(InputFieldException.class)
    public ResponseEntity<InputFieldErrorResponse> inputFieldExceptionHandler(final InputFieldException exception) {
        logInfo(exception);
        return ResponseEntity
                .status(exception.getStatus())
                .body(InputFieldErrorResponse.from(exception));
    }

    @ExceptionHandler(InfrastructureMalfunctionException.class)
    public ResponseEntity<ErrorResponse> wrongConfigurationOfInfrastructureException(final InfrastructureMalfunctionException exception) {
        logWarn(exception);
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(ZzimkkongException.class)
    public ResponseEntity<ErrorResponse> zzimkkongExceptionHandler(final ZzimkkongException exception) {
        logInfo(exception);
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<InputFieldErrorResponse> invalidArgumentHandler(final MethodArgumentNotValidException exception) {
        logInfo(exception);
        return ResponseEntity.badRequest().body(InputFieldErrorResponse.from(exception));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> invalidParamHandler(final ConstraintViolationException exception) {
        logInfo(exception);
        return ResponseEntity.badRequest().body(ErrorResponse.from(exception));
    }

    @ExceptionHandler({InvalidFormatException.class, HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> invalidFormatHandler(final Exception exception) {
        logInfo(exception);
        return ResponseEntity.badRequest().body(ErrorResponse.invalidFormat());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> invalidDataAccessHandler(final DataAccessException exception) {
        logWarn(exception);
        return ResponseEntity.internalServerError().body(ErrorResponse.internalServerError());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandledExceptionHandler(final Exception exception) {
        logWarn(exception);
        return ResponseEntity.internalServerError().body(ErrorResponse.internalServerError());
    }

    private void logInfo(Exception exception) {
        String logMessage = String.format(MESSAGE_FORMAT, exception.getMessage(), value(TRACE_ID_KEY, getTraceId()));
        log.info(logMessage, exception);
    }

    private void logWarn(Exception exception) {
        String logMessage = String.format(MESSAGE_FORMAT, exception.getMessage(), value(TRACE_ID_KEY, getTraceId()));
        log.warn(logMessage, exception);
    }

    private Object getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, RequestRejectedException requestRejectedException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
}
