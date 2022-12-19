package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import com.woowacourse.zzimkkong.domain.oauth.GithubUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.GoogleUserInfo;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.infrastructure.auth.AuthorizationExtractor;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMemberByOauth;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class AuthControllerTest extends AcceptanceTest {
    @Test
    @DisplayName("유효한 정보의 로그인 요청이 오면 토큰을 발급한다.")
    void login() {
        // given
        saveMember(memberSaveRequest);

        // when
        ExtractableResponse<Response> response = login(loginRequest);
        TokenResponse responseBody = response.body().as(TokenResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseBody.getAccessToken()).isInstanceOf(String.class);
    }

    @Test
    @DisplayName("Github Oauth 로그인 요청이 오면 토큰을 발급한다.")
    void loginByGithubOauth() {
        // given
        OauthProvider oauthProvider = OauthProvider.GITHUB;
        saveMemberByOauth(new OauthMemberSaveRequest(NEW_EMAIL, NEW_USER_NAME, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, ORGANIZATION, oauthProvider.name()));
        String code = "example-code";

        given(githubRequester.supports(OauthProvider.GITHUB))
                .willReturn(true);
        given(githubRequester.getUserInfoByCode(code))
                .willReturn(GithubUserInfo.from(Map.of("email", NEW_EMAIL)));

        // when
        ExtractableResponse<Response> response = loginByOauth(oauthProvider, code);
        TokenResponse responseBody = response.body().as(TokenResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseBody.getAccessToken()).isInstanceOf(String.class);

    }

    @Test
    @DisplayName("Google Oauth 로그인 요청이 오면 토큰을 발급한다.")
    void loginByGoogleOauth() {
        // given
        OauthProvider oauthProvider = OauthProvider.GOOGLE;
        saveMemberByOauth(new OauthMemberSaveRequest(NEW_EMAIL, NEW_USER_NAME, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, ORGANIZATION, oauthProvider.name()));
        String code = "example-code";

        given(googleRequester.supports(any(OauthProvider.class)))
                .willReturn(true);
        given(googleRequester.getUserInfoByCode(anyString()))
                .willReturn(GoogleUserInfo.from(
                        Map.of("id", "123",
                                "email", NEW_EMAIL)));

        // when
        ExtractableResponse<Response> response = loginByOauth(oauthProvider, code);
        TokenResponse responseBody = response.body().as(TokenResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseBody.getAccessToken()).isInstanceOf(String.class);
    }

    @Test
    @DisplayName("유효한 토큰으로 요청이 오면 200 ok가 반환된다.")
    void validToken() {
        // given
        saveMember(memberSaveRequest);
        String accessToken = AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken();

        //when
        ExtractableResponse<Response> response = token(accessToken, "success");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 요청이 오면 401 UnAuthorized가 반환된다.")
    void invalidToken() {
        //given
        String invalidToken = "strangeTokenIsComing";

        //when
        ExtractableResponse<Response> response = token(invalidToken, "fail");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    static String getToken() {
        ExtractableResponse<Response> loginResponse = login(loginRequest);

        TokenResponse responseBody = loginResponse.body().as(TokenResponse.class);
        return responseBody.getAccessToken();
    }

    static ExtractableResponse<Response> login(final LoginRequest loginRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/login", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/members/login/token")
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> loginByOauth(final OauthProvider oauthProvider, final String code) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/login/oauth/" + oauthProvider.name(), getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/members/" + oauthProvider + "/login/token?code=" + code)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> token(final String token, final String docName) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", token)
                .filter(document("member/token/" + docName, getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/members/token")
                .then().log().all().extract();
    }
}
