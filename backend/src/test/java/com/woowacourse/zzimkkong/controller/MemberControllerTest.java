package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.MemberSaveRequestDto;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemberControllerTest extends AcceptanceTest {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "루터";

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("정상적인 회원가입 입력이 들어오면 회원 정보를 저장한다")
    @Test
    void join() {
        MemberSaveRequestDto memberSaveRequestDto = new MemberSaveRequestDto(EMAIL, PASSWORD, ORGANIZATION);

        saveMember(memberSaveRequestDto);

        assertTrue(memberRepository.existsByEmail(EMAIL));
    }

    @DisplayName("중복된 이메일을 입력하면 400 에러를 반환한다")
    @Test
    void validateEmail() {
        MemberSaveRequestDto memberSaveRequestDto = new MemberSaveRequestDto(EMAIL, PASSWORD, ORGANIZATION);

        saveMember(memberSaveRequestDto);

        ExtractableResponse<Response> response = validateDuplicateEmail();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> validateDuplicateEmail() {
        return RestAssured
                .given().log().all()
                .queryParam("email", EMAIL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/members")
                .then().log().all().extract();
    }

    private void saveMember(final MemberSaveRequestDto memberSaveRequestDto) {
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberSaveRequestDto)
                .when().post("/api/members")
                .then().log().all();
    }
}
