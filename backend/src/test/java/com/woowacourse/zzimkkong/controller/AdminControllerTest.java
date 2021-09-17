package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.admin.MembersResponse;
import com.woowacourse.zzimkkong.dto.admin.PageInfo;
import com.woowacourse.zzimkkong.dto.member.MemberFindResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static com.woowacourse.zzimkkong.DocumentUtils.getRequestSpecification;
import static org.assertj.core.api.Assertions.assertThat;

class AdminControllerTest extends AcceptanceTest {
    @Test
    @DisplayName("올바른 로그인이 들어오면 200 ok를 반환한다.")
    void postLogin() {
        // given, when
        String id = "zzimkkong";
        String password = "zzimkkong1!";
        ExtractableResponse<Response> response = login(id, password);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("모든 회원을 조회한다.")
    void getMembers() {
        // given
        Member pobi = new Member(memberSaveRequest.getEmail(), memberSaveRequest.getPassword(), memberSaveRequest.getOrganization());

        MembersResponse membersResponse = MembersResponse.from(
                List.of(MemberFindResponse.from(pobi)),
                PageInfo.from(0, 1, 20, 1)
        );

        // when
        ExtractableResponse<Response> response = members();
        MembersResponse actual = response.body().as(MembersResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(membersResponse);
    }

    static ExtractableResponse<Response> login(String id, String password) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/admin/api/login?id=" + id + "&password=" + password)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> members() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().get("/admin/api/members")
                .then().log().all().extract();
    }
}
