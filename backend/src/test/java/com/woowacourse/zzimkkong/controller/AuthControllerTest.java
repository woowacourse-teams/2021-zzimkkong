package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.LoginRequest;
import com.woowacourse.zzimkkong.dto.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.TokenResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.woowacourse.zzimkkong.controller.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class AuthControllerTest extends AcceptanceTest {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "루터";

    @DisplayName("유효한 정보의 로그인 요청이 오면 200 ok와 토큰을 발급한다.")
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
}
