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
    private String spaceSaveApi;
    private Long beSpaceId;
    private Long feSpaceId;

    @BeforeEach
    void setUp() {
        saveMember(memberSaveRequest);
        saveMap("/api/managers/maps", mapCreateRequest);
        spaceSaveApi = "/api/managers/maps/" + LUTHER.getId() + "/spaces";
        ExtractableResponse<Response> saveBeSpaceResponse = saveSpace(spaceSaveApi, beSpaceCreateUpdateRequest);
        ExtractableResponse<Response> saveFe1SpaceResponse = saveSpace(spaceSaveApi, feSpaceCreateUpdateRequest);

        beSpaceId = Long.valueOf(saveBeSpaceResponse.header("location").split("/")[6]);
        feSpaceId = Long.valueOf(saveFe1SpaceResponse.header("location").split("/")[6]);

        BE = new Space.Builder()
                .id(beSpaceId)
                .name("백엔드 강의실")
                .color(BE.getColor())
                .map(LUTHER)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(BE_SETTING)
                .build();

        FE1 = new Space.Builder()
                .id(feSpaceId)
                .name("프론트엔드 강의실1")
                .color(FE1.getColor())
                .map(LUTHER)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();
    }

    @DisplayName("space 정보가 들어오면 space를 저장한다")
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
                "monday, tuesday, wednesday, thursday, friday, saturday, sunday"
        );

        SpaceCreateUpdateRequest newSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "잠실우리집",
                "#CCFFE5",
                "우리집",
                SPACE_DRAWING,
                newSettingsRequest
        );

        // when
        ExtractableResponse<Response> response = saveSpace(spaceSaveApi, newSpaceCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("space 정보 중 주어지지 않은 필드를 디폴트 값으로 저장한다")
    @Test
    void save_default() {
        // given, when
        SettingsRequest settingsRequest = new SettingsRequest(
                LocalTime.of(0,0),
                LocalTime.of(18,0),
                null,
                null,
                null,
                null,
                null
        );

        SpaceCreateUpdateRequest defaultSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "잠실우리집",
                "#CCFFE5",
                "우리집",
                SPACE_DRAWING,
                settingsRequest
        );

        Setting defaultSetting = new Setting.Builder()
                .availableStartTime(LocalTime.of(0, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(1440)
                .reservationEnable(true)
                .enabledDayOfWeek("monday, tuesday, wednesday, thursday, friday, saturday, sunday")
                .build();

        Space defaultSpace = new Space.Builder()
                .name(defaultSpaceCreateUpdateRequest.getName())
                .color(defaultSpaceCreateUpdateRequest.getColor())
                .description(defaultSpaceCreateUpdateRequest.getDescription())
                .setting(defaultSetting)
                .area(SPACE_DRAWING)
                .build();

        ExtractableResponse<Response> response = saveSpace(spaceSaveApi, defaultSpaceCreateUpdateRequest);

        // then
        String api = response.header("location");

        ExtractableResponse<Response> findResponse = findSpace(api);
        SpaceFindDetailResponse actualSpaceFindDetailResponse = findResponse.as(SpaceFindDetailResponse.class);
        SpaceFindDetailResponse expectedSpaceFindDetailResponse = SpaceFindDetailResponse.from(defaultSpace);

        assertThat(actualSpaceFindDetailResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedSpaceFindDetailResponse);
    }

    @DisplayName("spaceId를 받아 해당 공간에 대한 정보를 조회한다.")
    @Test
    void find() {
        // given, when
        String api = spaceSaveApi + "/" + beSpaceId;
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

    @DisplayName("전체 공간에 대한 정보를 조회한다.")
    @Test
    void findAll() {
        // given, when
        ExtractableResponse<Response> response = findAllSpace(spaceSaveApi);
        SpaceFindAllResponse actual = response.body().as(SpaceFindAllResponse.class);
        SpaceFindAllResponse expected = SpaceFindAllResponse.from(List.of(BE, FE1));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    @DisplayName("공간을 수정한다.")
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
                "monday, tuesday, wednesday, thursday, friday, saturday, sunday"
        );

        SpaceCreateUpdateRequest updateSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "바다",
                "#CCCCFF",
                "장미아파트",
                SPACE_DRAWING,
                settingsRequest
        );

        String api = "/api/managers/maps/" + LUTHER.getId() + "/spaces/" + beSpaceId;
        ExtractableResponse<Response> response = updateSpace(api, updateSpaceCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("공간을 삭제한다.")
    @Test
    void delete() {
        // given, when
        String api = "/api/managers/maps/" + LUTHER.getId() + "/spaces/" + beSpaceId;
        ExtractableResponse<Response> response = deleteSpace(api);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    static ExtractableResponse<Response> saveSpace(final String api, final SpaceCreateUpdateRequest spaceCreateRequest) {
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

    private ExtractableResponse<Response> findAllSpace(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("space/getAll", getRequestPreprocessor(), getResponsePreprocessor()))
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


