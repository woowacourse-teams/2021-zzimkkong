package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalTime;
import java.util.List;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.AuthControllerTest.login;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class SpaceControllerTest extends AcceptanceTest {
    private final String invalidToken = "rubbishToken";
    @Autowired
    private MemberRepository members;
    @Autowired
    private MapRepository maps;
    @Autowired
    private SpaceRepository spaces;
    private String token;

    private SpaceCreateUpdateRequest spaceCreateUpdateRequest;

    @BeforeEach
    void setUp() {
        // todo API가 생성되면 repository를 사용하지 않고 테스트 데이터를 저장하기 -- 김김
        members.save(POBI);
        maps.save(LUTHER);
        spaces.save(BE);
        spaces.save(FE1);

        SettingsRequest settingsRequest = new SettingsRequest(
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                30,
                60,
                120,
                true,
                "Monday, Tuesday"
        );

        spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "잠실우리집",
                "우리집",
                "프론트 화이팅",
                settingsRequest,
                "이미지 입니다"
        );

        LoginRequest pobiLoginRequest = new LoginRequest(POBI.getEmail(), POBI.getPassword());
        ExtractableResponse<Response> loginResponse = login(pobiLoginRequest);
        TokenResponse responseBody = loginResponse.body().as(TokenResponse.class);
        token = responseBody.getAccessToken();
    }

    @DisplayName("올바른 토큰이 주어질 때, space 정보가 들어오면 space를 저장한다")
    @Test
    void save() {
        // given, when
        ExtractableResponse<Response> response = saveSpace(token, 1L, spaceCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("토큰이 검증되지 않는다면, space를 저장할 수 없다")
    @Test
    void save_invalid() {
        // given, when
        ExtractableResponse<Response> response = saveSpace(invalidToken, 1L, spaceCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("올바른 토큰이 주어질 때, spaceId를 받아 해당 공간에 대한 정보를 조회한다.")
    @Test
    void find() {
        // given, when
        ExtractableResponse<Response> response = findSpace(token, LUTHER.getId(), BE.getId());
        SpaceFindDetailResponse actual = response.body().as(SpaceFindDetailResponse.class);
        SpaceFindDetailResponse expected = SpaceFindDetailResponse.from(BE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @DisplayName("토큰이 검증되지 않는다면, 해당 공간에 대한 정보를 조회할 수 없다.")
    @Test
    void find_invalidToken() {
        // given, when
        ExtractableResponse<Response> response = findSpace(invalidToken, LUTHER.getId(), BE.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("올바른 토큰이 주어질 때, 전체 공간에 대한 정보를 조회한다.")
    @Test
    void findAll() {
        // given, when
        ExtractableResponse<Response> response = findAllSpace(token, LUTHER.getId());
        SpaceFindAllResponse actual = response.body().as(SpaceFindAllResponse.class);
        SpaceFindAllResponse expected = SpaceFindAllResponse.from(List.of(BE, FE1));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @DisplayName("토큰이 검증되지 않는다면, 전체 공간에 대한 정보를 조회할 수 없다.")
    @Test
    void findAll_invalidToken() {
        // given, when
        ExtractableResponse<Response> response = findAllSpace(invalidToken, LUTHER.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private ExtractableResponse<Response> saveSpace(final String token, final Long mapId, final SpaceCreateUpdateRequest spaceCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", "Bearer " + token)
                .filter(document("space/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(spaceCreateUpdateRequest)
                .when().post("/api/managers/maps/" + mapId.toString() + "/spaces")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findSpace(final String token, final Long mapId, final Long spaceId) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", "Bearer " + token)
                .filter(document("space/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/managers/maps/" + mapId.toString() + "/spaces/" + spaceId.toString())
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllSpace(final String token, final Long mapId) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", "Bearer " + token)
                .filter(document("space/get_all", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/managers/maps/" + mapId.toString() + "/spaces/")
                .then().log().all().extract();
    }
}


