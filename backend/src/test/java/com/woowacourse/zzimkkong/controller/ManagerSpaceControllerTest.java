package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.infrastructure.auth.AuthorizationExtractor;
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

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class ManagerSpaceControllerTest extends AcceptanceTest {
    private String spaceApi;
    private Long beSpaceId;
    private Space be;
    private Space fe;

    @BeforeEach
    void setUp() {
        String lutherId = saveMap("/api/managers/maps", mapCreateUpdateRequest).header("location").split("/")[4];
        spaceApi = "/api/managers/maps/" + lutherId + "/spaces";
        ExtractableResponse<Response> saveBeSpaceResponse = saveSpace(spaceApi, beSpaceCreateUpdateRequest);
        ExtractableResponse<Response> saveFe1SpaceResponse = saveSpace(spaceApi, feSpaceCreateUpdateRequest);

        beSpaceId = Long.valueOf(saveBeSpaceResponse.header("location").split("/")[6]);
        Long feSpaceId = Long.valueOf(saveFe1SpaceResponse.header("location").split("/")[6]);

        Member pobi = new Member(EMAIL, passwordEncoder.encode(PW), ORGANIZATION);
        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);
        Setting beSetting = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Setting feSetting = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();

        be = Space.builder()
                .id(beSpaceId)
                .name(BE_NAME)
                .color(BE_COLOR)
                .map(luther)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(beSetting)
                .build();

        fe = Space.builder()
                .id(feSpaceId)
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .description(FE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(feSetting)
                .build();
    }

    @Test
    @DisplayName("space ????????? ???????????? space??? ????????????")
    void save() {
        // given
        SettingsRequest newSettingsRequest = new SettingsRequest(
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                30,
                60,
                120,
                true,
                EnabledDayOfWeekDto.from("monday, tuesday, wednesday, thursday, friday, saturday, sunday")
        );

        SpaceCreateUpdateRequest newSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "???????????????",
                "#CCFFE5",
                "?????????",
                SPACE_DRAWING,
                newSettingsRequest,
                MAP_SVG
        );

        // when
        ExtractableResponse<Response> response = saveSpace(spaceApi, newSpaceCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("space ?????? ??? ???????????? ?????? ????????? ????????? ????????? ????????????")
    void save_default() {
        // given, when
        SettingsRequest settingsRequest = new SettingsRequest(
                LocalTime.of(0, 0),
                LocalTime.of(18, 0),
                null,
                null,
                null,
                null,
                null
        );

        SpaceCreateUpdateRequest defaultSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "???????????????",
                "#CCFFE5",
                "?????????",
                SPACE_DRAWING,
                settingsRequest,
                MAP_SVG
        );

        Setting defaultSetting = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        LocalTime.of(0, 0),
                        LocalTime.of(18, 0)))
                .reservationTimeUnit(TimeUnit.from(10))
                .reservationMinimumTimeUnit(TimeUnit.from(10))
                .reservationMaximumTimeUnit(TimeUnit.from(120))
                .reservationEnable(true)
                .enabledDayOfWeek("monday, tuesday, wednesday, thursday, friday, saturday, sunday")
                .build();

        Space defaultSpace = Space.builder()
                .name(defaultSpaceCreateUpdateRequest.getName())
                .color(defaultSpaceCreateUpdateRequest.getColor())
                .description(defaultSpaceCreateUpdateRequest.getDescription())
                .setting(defaultSetting)
                .area(SPACE_DRAWING)
                .build();

        ExtractableResponse<Response> response = saveSpace(spaceApi, defaultSpaceCreateUpdateRequest);

        // then
        String api = response.header("location");

        ExtractableResponse<Response> findResponse = findSpace(api);
        SpaceFindDetailResponse actualSpaceFindDetailResponse = findResponse.as(SpaceFindDetailResponse.class);
        SpaceFindDetailResponse expectedSpaceFindDetailResponse = SpaceFindDetailResponse.from(defaultSpace);

        assertThat(actualSpaceFindDetailResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedSpaceFindDetailResponse);
    }

    @Test
    @DisplayName("spaceId??? ?????? ?????? ????????? ?????? ????????? ????????????.")
    void find() {
        // given, when
        String api = spaceApi + "/" + beSpaceId;
        ExtractableResponse<Response> response = findSpace(api);
        SpaceFindDetailResponse actual = response.body().as(SpaceFindDetailResponse.class);
        SpaceFindDetailResponse expected = SpaceFindDetailResponse.from(be);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringActualNullFields()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("?????? ????????? ?????? ????????? ????????????.")
    void findAll() {
        // given, when
        ExtractableResponse<Response> response = findAllSpace(spaceApi);
        SpaceFindAllResponse actual = response.body().as(SpaceFindAllResponse.class);
        SpaceFindAllResponse expected = SpaceFindAllResponse.from(List.of(be, fe));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("????????? ????????????.")
    void update() {
        // given, when
        SettingsRequest settingsRequest = new SettingsRequest(
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                30,
                60,
                120,
                false,
                EnabledDayOfWeekDto.from("monday, tuesday, wednesday, thursday, friday, saturday, sunday")
        );

        SpaceCreateUpdateRequest updateSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "??????",
                "#CCCCFF",
                "???????????????",
                SPACE_DRAWING,
                settingsRequest,
                MAP_SVG
        );

        String api = spaceApi + "/" + beSpaceId;
        ExtractableResponse<Response> response = updateSpace(api, updateSpaceCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("????????? ????????????.")
    void delete() {
        // given, when
        String api = spaceApi + "/" + beSpaceId;
        SpaceDeleteRequest spaceDeleteRequest = new SpaceDeleteRequest(MAP_SVG);

        ExtractableResponse<Response> response = deleteSpace(api, spaceDeleteRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    static ExtractableResponse<Response> saveSpace(final String api, final SpaceCreateUpdateRequest spaceCreateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("space/manager/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(spaceCreateRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllSpace(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("space/manager/getAll", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findSpace(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("space/manager/get", getRequestPreprocessor(), getResponsePreprocessor()))
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
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("space/manager/put", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(spaceCreateUpdateRequest)
                .when().put(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteSpace(final String api, final SpaceDeleteRequest spaceDeleteRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("space/manager/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(spaceDeleteRequest)
                .when().delete(api)
                .then().log().all().extract();
    }
}
