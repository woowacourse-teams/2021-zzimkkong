package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailResponse;
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
import java.util.List;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.AuthControllerTest.getToken;
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

        SpaceCreateUpdateRequest spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "백엔드 강의실",
                "시니컬하네",
                "area",
                settingsRequest,
                "이미지 입니다"
        );
        String saveSpaceApi = "/api/managers/maps/1/spaces";
        saveSpace(saveSpaceApi, spaceCreateUpdateRequest);
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

        SpaceCreateUpdateRequest newSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "잠실우리집",
                "우리집",
                "프론트 화이팅",
                newSettingsRequest,
                "이미지 입니다"
        );

        // when
        String saveSpaceApi = "/api/managers/maps/1/spaces";
        ExtractableResponse<Response> response = saveSpace(saveSpaceApi, newSpaceCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
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

        SpaceCreateUpdateRequest defaultSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
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
        ExtractableResponse<Response> response = saveSpace(saveSpaceApi, defaultSpaceCreateUpdateRequest);

        // then
        String api = response.header("location");

        ExtractableResponse<Response> findResponse = findSpace(api);
        SpaceFindDetailResponse actualSpaceFindDetailResponse = findResponse.as(SpaceFindDetailResponse.class);
        SpaceFindDetailResponse expectedSpaceFindDetailResponse = SpaceFindDetailResponse.from(defaultSpace);

        assertThat(actualSpaceFindDetailResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedSpaceFindDetailResponse);
    }

    @DisplayName("올바른 토큰이 주어질 때, spaceId를 받아 해당 공간에 대한 정보를 조회한다.")
    @Test
    void find() {
        // given, when
        String api = "/api/managers/maps/1/spaces/1";
        ExtractableResponse<Response> response = findSpace(api);
        SpaceFindDetailResponse actual = response.body().as(SpaceFindDetailResponse.class);
        SpaceFindDetailResponse expected = SpaceFindDetailResponse.from(BE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringActualNullFields()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    @DisplayName("올바른 토큰이 주어질 때, 전체 공간에 대한 정보를 조회한다.")
    @Test
    void findAll() {
        // given
        SettingsRequest feSettingsRequest = new SettingsRequest(
                LocalTime.of(0, 0),
                LocalTime.of(23, 59),
                10,
                10,
                1440,
                true,
                null
        );

        SpaceCreateUpdateRequest feSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "프론트엔드 강의실1",
                "시니컬하네",
                "area",
                feSettingsRequest,
                "이미지 입니다"
        );

        String api = "/api/managers/maps/1/spaces";
        saveSpace(api, feSpaceCreateUpdateRequest);

        // when
        ExtractableResponse<Response> response = findAllSpace(api);
        SpaceFindAllResponse actual = response.body().as(SpaceFindAllResponse.class);
        SpaceFindAllResponse expected = SpaceFindAllResponse.from(List.of(BE, FE1));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    @DisplayName("올바른 토큰이 주어질 때, 공간을 수정한다.")
    @Test
    void update() {
        // given, when
        SettingsRequest settingsRequest = new SettingsRequest(
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                40,
                80,
                130,
                false,
                "Monday, Tuesday"
        );

        SpaceCreateUpdateRequest updateSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "바다",
                "장미아파트",
                "장미",
                settingsRequest,
                "장미아파트 이미지"
        );

        String api = "/api/managers/maps/" + LUTHER.getId() + "/spaces/" + BE.getId();
        ExtractableResponse<Response> response = updateSpace(api, updateSpaceCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("올바른 토큰이 주어질 때, 공간을 삭제한다.")
    @Test
    void delete() {
        // given, when
        String api = "/api/managers/maps/" + LUTHER.getId() + "/spaces/" + BE.getId();
        ExtractableResponse<Response> response = deleteSpace(api);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> saveSpace(final String api, final SpaceCreateUpdateRequest spaceCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("space/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(spaceCreateUpdateRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllSpace(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("space/get_all", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
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

    private ExtractableResponse<Response> updateSpace(
            final String api,
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("space/put", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(spaceCreateUpdateRequest)
                .when().put(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteSpace(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("space/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(api)
                .then().log().all().extract();
    }
}


