package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.ErrorResponse;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class AuthControllerTest extends AcceptanceTest {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "루터";

    @DisplayName("유효한 정보의 로그인 요청이 오면 200 ok로 응답하며 토큰을 발급한다.")
    @Test
    void login() {
        // given
        saveMember(new MemberSaveRequest(EMAIL, PASSWORD, ORGANIZATION));

        // when
        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        ExtractableResponse<Response> response = login(loginRequest);
        TokenResponse responseBody = response.body().as(TokenResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseBody.getAccessToken()).isInstanceOf(String.class);
    }

    @DisplayName("유효하지 않은 정보의 로그인 요청이 오면 400 Bad Request를 응답한다.")
    @Test
    void loginWithInvalidInformation() {
        // given
        saveMember(new MemberSaveRequest(EMAIL, PASSWORD, ORGANIZATION));

        // when
        LoginRequest loginRequest = new LoginRequest(EMAIL, "WrongPassword1234");
        ExtractableResponse<Response> response = login(loginRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("유효한 토큰으로 요청이 오면 200 ok가 반환된다.")
    void validToken() {
        // given
        saveMember(new MemberSaveRequest(EMAIL, PASSWORD, ORGANIZATION));

        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        ExtractableResponse<Response> loginResponse = login(loginRequest);
        TokenResponse responseBody = loginResponse.body().as(TokenResponse.class);

        //when
        ExtractableResponse<Response> response = token(responseBody.getAccessToken(), "success");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 요청이 오면 401 UnAuthorized가 반환된다.")
    void invalidToken() {
        //given
        String invalidToken = "strangeTokenIsComing";

        //when
        ExtractableResponse<Response> response = token(invalidToken,"fail");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private ExtractableResponse<Response> login(final LoginRequest loginRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/login", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/login/token")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> token(String token, String docName) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", "Bearer " + token)
                .filter(document("member/token/" + docName, getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/members/token")
                .then().log().all().extract();
    }
}
