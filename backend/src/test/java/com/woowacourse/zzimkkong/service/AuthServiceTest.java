package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.exception.authorization.OauthProviderMismatchException;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import com.woowacourse.zzimkkong.exception.member.PasswordMismatchException;
import com.woowacourse.zzimkkong.infrastructure.JwtUtils;
import com.woowacourse.zzimkkong.infrastructure.oauth.OauthHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthServiceTest extends ServiceTest {
    private Member pobi;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, passwordEncoder.encode(PW), ORGANIZATION);
    }

    @MockBean
    private OauthHandler oauthHandler;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("회원 로그인 요청이 옳다면 토큰을 발급한다.")
    void login() {
        //given
        LoginRequest loginRequest = new LoginRequest(EMAIL, PW);
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
        LoginRequest loginRequest = new LoginRequest(EMAIL, PW);

        //when
        given(members.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(NoSuchMemberException.class);
    }


    @Test
    @DisplayName("회원 로그인 요청 시, 비밀번호가 일치하지 않으면 오류가 발생한다.")
    void loginMismatchException() {
        //given
        LoginRequest loginRequest = new LoginRequest(EMAIL, PW);

        //when
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(new Member(EMAIL, passwordEncoder.encode("wrong_password"), ORGANIZATION)));

        //then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @ParameterizedTest
    @EnumSource(OauthProvider.class)
    @DisplayName("Oauth 인증 코드를 통해 토큰을 발급한다.")
    void loginByOauth(OauthProvider oauthProvider) {
        // given
        String mockCode = "Mock Code from OauthProvider";

        OauthUserInfo mockOauthUserInfo = mock(OauthUserInfo.class);
        given(oauthHandler.getUserInfoFromCode(any(OauthProvider.class), anyString()))
                .willReturn(mockOauthUserInfo);
        given(mockOauthUserInfo.getEmail())
                .willReturn(EMAIL);
        given(members.findByEmail(EMAIL))
                .willReturn(Optional.of(new Member(EMAIL, ORGANIZATION, oauthProvider)));

        // when
        TokenResponse tokenResponse = authService.loginByOauth(oauthProvider, mockCode);

        // then
        String accessToken = tokenResponse.getAccessToken();
        assertThat(accessToken).isNotNull();
        jwtUtils.validateToken(accessToken);
    }

    @ParameterizedTest
    @EnumSource(OauthProvider.class)
    @DisplayName("존재하지 않는 이메일로 oauth 로그인 시 오류가 발생한다.")
    void loginByOauthInvalidEmailException(OauthProvider oauthProvider) {
        // given
        String mockCode = "Mock Code from OauthProvider";

        OauthUserInfo mockOauthUserInfo = mock(OauthUserInfo.class);
        given(oauthHandler.getUserInfoFromCode(any(OauthProvider.class), anyString()))
                .willReturn(mockOauthUserInfo);
        given(mockOauthUserInfo.getEmail())
                .willReturn(EMAIL);
        given(members.findByEmail(EMAIL))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> authService.loginByOauth(oauthProvider, mockCode))
                .isInstanceOf(NoSuchMemberException.class);
    }

    @ParameterizedTest
    @EnumSource(OauthProvider.class)
    @DisplayName("회원가입한 provider와 다른 provider로 같은 이메일 oauth 로그인 시 오류가 발생한다.")
    void loginByOauthInvalidProviderException(OauthProvider oauthProvider) {
        // given
        String mockCode = "Mock Code from OauthProvider";
        OauthProvider anotherProvider = Arrays.stream(OauthProvider.values())
                .filter(provider -> !provider.equals(oauthProvider))
                .findAny()
                .get();

        System.out.println("12312312"+ anotherProvider.name());
        OauthUserInfo mockOauthUserInfo = mock(OauthUserInfo.class);
        given(oauthHandler.getUserInfoFromCode(any(OauthProvider.class), anyString()))
                .willReturn(mockOauthUserInfo);
        given(mockOauthUserInfo.getEmail())
                .willReturn(EMAIL);
        given(members.findByEmail(EMAIL))
                .willReturn(Optional.of(new Member(EMAIL, ORGANIZATION, anotherProvider)));

        // when, then
        assertThatThrownBy(() -> authService.loginByOauth(oauthProvider, mockCode))
                .isInstanceOf(OauthProviderMismatchException.class);
    }
}
