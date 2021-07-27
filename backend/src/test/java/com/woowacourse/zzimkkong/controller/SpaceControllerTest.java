package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceFindResponse;
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

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.AuthControllerTest.login;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class SpaceControllerTest extends AcceptanceTest {
    @Autowired
    private MemberRepository members;
    @Autowired
    private MapRepository maps;
    @Autowired
    private SpaceRepository spaces;
    private String token;
    private SpaceCreateRequest spaceCreateRequest;

    @BeforeEach
    void setUp() {
        // todo API가 생성되면 repository를 사용하지 않고 테스트 데이터를 저장하기 -- 김김
        members.save(POBI);
        maps.save(LUTHER);
        spaces.save(BE);

        SettingsRequest settingsRequest = new SettingsRequest(
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                30,
                60,
                120,
                true,
                "Monday, Tuesday"
        );

        spaceCreateRequest = new SpaceCreateRequest(
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
        ExtractableResponse<Response> response = saveSpace(1L, spaceCreateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("올바른 토큰이 주어질 때, spaceId를 받아 해당 공간에 대한 정보를 조회한다.")
    @Test
    void find() {
        // given, when
        ExtractableResponse<Response> response = findSpace(LUTHER.getId(), BE.getId());
        SpaceFindResponse actual = response.body().as(SpaceFindResponse.class);
        SpaceFindResponse expected = SpaceFindResponse.from(BE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @DisplayName("올바른 토큰이 주어질 때, space 정보 중 주어지지 않은 필드를 디폴트 값으로 저장한다")
    @Test
    void save_default() {
        // given, when
        SettingsRequest settingsRequest = new SettingsRequest(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        SpaceCreateRequest defaultSpaceCreateRequest = new SpaceCreateRequest(
                "잠실우리집",
                "우리집",
                "프론트 화이팅",
                settingsRequest,
                "이미지 입니다"
        );

        Space defaultSpace = new Space.Builder()
                .id(2L)
                .name("잠실우리집")
                .description("우리집")
                .area("프론트 화이팅")
                .availableStartTime(LocalTime.of(0, 0))
                .availableEndTime(LocalTime.of(23, 59))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(1440)
                .reservationEnable(true)
                .disabledWeekdays(null)
                .mapImage("이미지 입니다")
                .build();

        ExtractableResponse<Response> response = saveSpace(1L, defaultSpaceCreateRequest);

        // then
        String api = response.header("location");
        String[] split = api.split("/");
        Long mapId = Long.valueOf(split[4]);
        Long spaceId = Long.valueOf(split[6]);

        ExtractableResponse<Response> findResponse = findSpace(mapId, spaceId);
        SpaceFindResponse actualSpaceFindResponse = findResponse.as(SpaceFindResponse.class);
        SpaceFindResponse expectedSpaceFindResponse = SpaceFindResponse.from(defaultSpace);

        assertThat(actualSpaceFindResponse).usingRecursiveComparison().isEqualTo(expectedSpaceFindResponse);
    }

    private ExtractableResponse<Response> saveSpace(final Long mapId, final SpaceCreateRequest spaceCreateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", "Bearer " + token)
                .filter(document("space/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(spaceCreateRequest)
                .when().post("/api/managers/maps/" + mapId.toString() + "/spaces")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findSpace(final Long mapId, final Long spaceId) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", "Bearer " + token)
                .filter(document("space/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/managers/maps/" + mapId.toString() + "/spaces/" + spaceId.toString())
                .then().log().all().extract();
    }
}


