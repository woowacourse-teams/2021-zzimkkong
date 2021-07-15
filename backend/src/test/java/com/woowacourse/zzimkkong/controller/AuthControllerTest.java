package com.woowacourse.zzimkkong.controller;

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

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class AuthControllerTest extends AcceptanceTest {
    @DisplayName("유효한 정보의 로그인 요청이 오면 토큰을 발급한다.")
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
