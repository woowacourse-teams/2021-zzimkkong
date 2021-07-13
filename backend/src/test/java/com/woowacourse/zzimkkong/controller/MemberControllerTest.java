package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.MemberSaveRequest;
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

class MemberControllerTest extends AcceptanceTest {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "루터";

    @DisplayName("정상적인 회원가입 입력이 들어오면 회원 정보를 저장한다")
    @Test
    void join() {
        //given
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(EMAIL, PASSWORD, ORGANIZATION);

        //when
        ExtractableResponse<Response> response = saveMember(memberSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("이메일 중복 확인 시, 중복되지 않은 이메일을 입력하면 200을 반환한다.")
    @Test
    void getMembers() {
        //given
        String email = "dusdn1702@naver.com";

        //when
        ExtractableResponse<Response> response = validateDuplicateEmail(email);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> validateDuplicateEmail(String email) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParam("email", email)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/members")
                .then().log().all().extract();
    }
}
