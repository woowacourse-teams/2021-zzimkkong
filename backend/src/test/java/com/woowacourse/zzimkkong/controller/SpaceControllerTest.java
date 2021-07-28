package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceFindResponse;
import com.woowacourse.zzimkkong.infrastructure.AuthorizationExtractor;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.CommonFixture.BE;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class SpaceControllerTest extends AcceptanceTest {
    @BeforeEach
    void setUp() {
        saveMember(memberSaveRequest);
        saveMap("/api/managers/maps", mapCreateRequest);

        SettingsRequest settingsRequest = new SettingsRequest(
                LocalTime.of(0, 0),
                LocalTime.of(23, 59),
                10,
                10,
                1440,
                true,
                null
        );

        SpaceCreateRequest spaceCreateRequest = new SpaceCreateRequest(
                "백엔드 강의실",
                "시니컬하네",
                "area",
                settingsRequest,
                "이미지 입니다"
        );
        String saveSpaceApi = "/api/managers/maps/1/spaces";
        saveSpace(saveSpaceApi, spaceCreateRequest);
    }

    @DisplayName("올바른 토큰이 주어질 때, space 정보가 들어오면 space를 저장한다")
    @Test
    void save() {
        // given
        SettingsRequest newSettingsRequest = new SettingsRequest(
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                20,
                60,
                100,
                true,
                "Monday, Tuesday"
        );

        SpaceCreateRequest newSpaceCreateRequest = new SpaceCreateRequest(
                "새로운공간",
                "우리집",
                "프론트 화이팅",
                newSettingsRequest,
                "이미지 입니다"
        );

        // when
        String saveSpaceApi = "/api/managers/maps/1/spaces";
        ExtractableResponse<Response> response = saveSpace(saveSpaceApi, newSpaceCreateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("올바른 토큰이 주어질 때, spaceId를 받아 해당 공간에 대한 정보를 조회한다.")
    @Test
    void find() {
        // given, when
        String api = "/api/managers/maps/1/spaces/1";
        ExtractableResponse<Response> response = findSpace(api);
        SpaceFindResponse actual = response.body().as(SpaceFindResponse.class);
        SpaceFindResponse expected = SpaceFindResponse.from(BE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringActualNullFields()
                .ignoringExpectedNullFields()
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

        Setting defaultSetting = new Setting.Builder()
                .availableStartTime(LocalTime.of(0, 0))
                .availableEndTime(LocalTime.of(23, 59))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(1440)
                .reservationEnable(true)
                .disabledWeekdays(null)
                .build();

        Space defaultSpace = new Space.Builder()
                .id(2L)
                .name("잠실우리집")
                .description("우리집")
                .area("프론트 화이팅")
                .setting(defaultSetting)
                .mapImage("이미지 입니다")
                .build();

        String saveSpaceApi = "/api/managers/maps/1/spaces";
        ExtractableResponse<Response> response = saveSpace(saveSpaceApi, defaultSpaceCreateRequest);

        // then
        String api = response.header("location");

        ExtractableResponse<Response> findResponse = findSpace(api);
        SpaceFindResponse actualSpaceFindResponse = findResponse.as(SpaceFindResponse.class);
        SpaceFindResponse expectedSpaceFindResponse = SpaceFindResponse.from(defaultSpace);

        assertThat(actualSpaceFindResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedSpaceFindResponse);
    }

    private ExtractableResponse<Response> saveSpace(final String api, final SpaceCreateRequest spaceCreateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("space/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(spaceCreateRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findSpace(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("space/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }
}


