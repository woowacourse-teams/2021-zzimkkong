package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.ErrorResponse;
import com.woowacourse.zzimkkong.exception.MemberException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler({MemberException.class})
    public ResponseEntity<ErrorResponse> memberExceptionHandler(MemberException exception) {
        return ResponseEntity.status(exception.getStatus()).body(new ErrorResponse(exception.getMessage()));
    }
}
