package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import com.woowacourse.zzimkkong.exception.member.PasswordMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class AuthServiceTest extends ServiceTest {
    private Member pobi;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
    }

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("회원 로그인 요청이 옳다면 토큰을 발급한다.")
    void login() {
        //given
        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));

        //when
        TokenResponse tokenResponse = authService.login(loginRequest);

        //then
        assertThat(tokenResponse).isNotNull();
    }

    @Test
    @DisplayName("이메일이 존재하지 않는 회원이 회원 로그인을 요청한다면 오류가 발생한다.")
    void loginException() {
        //given
        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

        //when
        given(members.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(NoSuchMemberException.class);
    }


    @Test
    @DisplayName("회원 로그인 요청 시, 이메일과 비밀번호가 일치하지 않으면 오류가 발생한다.")
    void loginMismatchException() {
        //given
        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

        //when
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(new Member(EMAIL, "wrong_password", ORGANIZATION)));

        //then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(PasswordMismatchException.class);
    }
}
