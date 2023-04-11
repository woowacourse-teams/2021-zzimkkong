package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekDto;
import com.woowacourse.zzimkkong.dto.space.SettingRequest;
import com.woowacourse.zzimkkong.dto.space.SettingsSummaryResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateUpdateRequest;
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
import static com.woowacourse.zzimkkong.controller.ManagerSpaceControllerTest.saveSpace;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class SettingControllerTest extends AcceptanceTest {
    private String spaceApi;
    private String lutherId;
    private Long beSpaceId;
    private Space be;
    private Space fe;

    private final SettingRequest beSettingRequest = new SettingRequest(
            BE_AVAILABLE_START_TIME,
            BE_AVAILABLE_END_TIME,
            BE_RESERVATION_TIME_UNIT.getMinutes(),
            BE_RESERVATION_MINIMUM_TIME_UNIT.getMinutes(),
            BE_RESERVATION_MAXIMUM_TIME_UNIT.getMinutes(),
            EnabledDayOfWeekDto.from(BE_ENABLED_DAY_OF_WEEK),
            0
    );
    private final SettingRequest beSettingRequest2 = new SettingRequest(
            LocalTime.of(11, 0),
            LocalTime.of(15, 0),
            TimeUnit.from(5).getMinutes(),
            TimeUnit.from(30).getMinutes(),
            TimeUnit.from(60).getMinutes(),
            EnabledDayOfWeekDto.from(BE_ENABLED_DAY_OF_WEEK),
            1
    );
    private final SpaceCreateUpdateRequest beSpaceCreateUpdateRequestUpgraded = new SpaceCreateUpdateRequest(
            BE_NAME,
            BE_COLOR,
            SPACE_DRAWING,
            MAP_SVG,
            BE_RESERVATION_ENABLE,
            List.of(beSettingRequest, beSettingRequest2)
    );

    @BeforeEach
    void setUp() {
        lutherId = saveMap("/api/managers/maps", mapCreateUpdateRequest).header("location").split("/")[4];
        spaceApi = "/api/managers/maps/" + lutherId + "/spaces";
        ExtractableResponse<Response> saveBeSpaceResponse = saveSpace(spaceApi, beSpaceCreateUpdateRequestUpgraded);
        ExtractableResponse<Response> saveFe1SpaceResponse = saveSpace(spaceApi, feSpaceCreateUpdateRequest);

        beSpaceId = Long.valueOf(saveBeSpaceResponse.header("location").split("/")[6]);
        Long feSpaceId = Long.valueOf(saveFe1SpaceResponse.header("location").split("/")[6]);

        Member pobi = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(passwordEncoder.encode(PW))
                .organization(ORGANIZATION)
                .build();
        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);
        Setting beSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .priorityOrder(1)
                .build();


        Setting beSetting2 = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        LocalTime.of(11, 0),
                        LocalTime.of(15, 0)))
                .reservationTimeUnit(TimeUnit.from(5))
                .reservationMinimumTimeUnit(TimeUnit.from(30))
                .reservationMaximumTimeUnit(TimeUnit.from(60))
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .priorityOrder(0)
                .build();

        Setting feSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .priorityOrder(0)
                .build();

        be = Space.builder()
                .id(beSpaceId)
                .name(BE_NAME)
                .color(BE_COLOR)
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .spaceSettings(Settings.toPrioritizedSettings(List.of(beSetting, beSetting2)))
                .build();

        fe = Space.builder()
                .id(feSpaceId)
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .spaceSettings(Settings.toPrioritizedSettings(List.of(feSetting)))
                .build();
    }

    @Test
    @DisplayName("특정 맵 특정 공간의 예약자들을 위한 (flat) 특정 일자의 예약 조건 (setting) 요약 메세지를 조회한다")
    void find_flat() {
        // given
        String api = "/api/maps/" + lutherId + "/spaces/" + beSpaceId + "/settings/summary";

        // when
        ExtractableResponse<Response> response = findFlat(api);
        SettingsSummaryResponse actualResponse = response.as(SettingsSummaryResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("특정 맵 특정 공간의 관리자들을 위한 (stack) 특정 일자의 예약 조건 (setting) 요약 메세지를 조회한다")
    void find_stack() {
        // given
        String api = "/api/maps/" + lutherId + "/spaces/" + beSpaceId + "/settings/summary";

        // when
        ExtractableResponse<Response> response = findStack(api);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("특정 맵 특정 공간의 예약자들을 위한 (flat) 전체 예약 조건 (setting) 요약 메세지를 조회한다")
    void find_flat_all() {
        // given
        String api = "/api/maps/" + lutherId + "/spaces/" + beSpaceId + "/settings/summary";

        // when
        ExtractableResponse<Response> response = findFlatAll(api);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("특정 맵 특정 공간의 관리자들을 위한 (stack) 전체 예약 조건 (setting) 요약 메세지를 조회한다")
    void find_stack_all() {
        // given
        String api = "/api/maps/" + lutherId + "/spaces/" + beSpaceId + "/settings/summary";

        // when
        ExtractableResponse<Response> response = findStackAll(api);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    static ExtractableResponse<Response> findFlat(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .param("selectedDateTime", BE_AM_TEN_ELEVEN_START_TIME_KST.toLocalDate() + "T12:00:00+09:00")
                .filter(document("setting/get_flat", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> findStack(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .param("selectedDateTime", BE_AM_TEN_ELEVEN_START_TIME_KST.toLocalDate() + "T12:00:00+09:00")
                .param("settingViewType", "STACK")
                .filter(document("setting/get_stack", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> findFlatAll(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("setting/get_flat_all", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> findStackAll(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .param("settingViewType", "STACK")
                .filter(document("setting/get_stack_all", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }
}
