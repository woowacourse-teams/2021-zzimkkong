package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.MemberSaveRequestDto;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

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

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberSaveRequestDto)
                .when().post("/api/members")
                .then().log().all();

        assertTrue(memberRepository.existsByEmail(EMAIL));
    }
}