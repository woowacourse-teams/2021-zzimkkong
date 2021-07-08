package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.LoginRequest;
import com.woowacourse.zzimkkong.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login/token")
    // todo Access Token을 응답
    public ResponseEntity<Void> login(@RequestBody @Valid final LoginRequest loginRequest) {
        authService.login(loginRequest);
        return ResponseEntity.ok().build();
    }
}
