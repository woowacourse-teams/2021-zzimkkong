package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/members")
public class AuthController {
    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login/token")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid final LoginRequest loginRequest) {
        return ResponseEntity.ok()
                .body(authService.login(loginRequest));
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{oauthProvider}/login/token")
    public ResponseEntity<TokenResponse> loginByOauth(@PathVariable OauthProvider oauthProvider, @RequestParam String code) {
        return ResponseEntity.ok()
                .body(authService.loginByOauth(oauthProvider, code));
    }
}
