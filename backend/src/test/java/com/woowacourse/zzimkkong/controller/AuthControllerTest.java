package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GithubUserInfo;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.OAuthMemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.infrastructure.AuthorizationExtractor;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.woowacourse.zzimkkong.Constants.EMAIL;
import static com.woowacourse.zzimkkong.Constants.ORGANIZATION;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMemberByOAuth;
import static org.assertj.core.api.Assertions.assertThat;
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

    // todo Parameterized 김샐
    @Test
    @DisplayName("OAuth를 통해 로그인할 수 있다.")
    void loginByOAuthParameterized() {
        // given
        OAuthProvider oAuthProvider = OAuthProvider.GITHUB;
        saveMemberByOAuth(new OAuthMemberSaveRequest(EMAIL, ORGANIZATION, oAuthProvider.name()));
        String code = "4%2F0AX4XfWjEXMLEdAqN4Bxqufcm8MIP1btBZY_nTeS_1M3b46MqSrq-h2A3Z2ydSOlZI1SpeA";

        given(githubRequester.supports(OAuthProvider.GITHUB))
                .willReturn(true);
        given(githubRequester.getUserInfoByCode(code))
                .willReturn(GithubUserInfo.from(Map.of("email", EMAIL)));

        // when
        ExtractableResponse<Response> response = loginByOAuth(oAuthProvider, code);
        TokenResponse responseBody = response.body().as(TokenResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseBody.getAccessToken()).isInstanceOf(String.class);

    }

    @Test
    @DisplayName("Google OAuth 로그인 요청이 오면 토큰을 발급한다.")
    void loginByOauth() {
        // given
        OAuthProvider oAuthProvider = OAuthProvider.GOOGLE;
        String code = "4%2F0AX4XfWjEXMLEdAqN4Bxqufcm8MIP1btBZY_nTeS_1M3b46MqSrq-h2A3Z2ydSOlZI1SpeA";

        // when
        ExtractableResponse<Response> response = loginByOAuth(oAuthProvider, code);
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
                .when().post("/api/login/token")
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> loginByOAuth(final OAuthProvider oAuthProvider, final String code) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/login/oauth", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/" + oAuthProvider + "/login/token?code=" + code)
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
