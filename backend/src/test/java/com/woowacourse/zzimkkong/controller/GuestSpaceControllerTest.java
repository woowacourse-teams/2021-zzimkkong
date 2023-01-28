package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.space.SpaceFindAllAvailabilityResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailResponse;
import com.woowacourse.zzimkkong.infrastructure.auth.AuthorizationExtractor;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashSet;
import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.ManagerSpaceControllerTest.saveSpace;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class GuestSpaceControllerTest extends AcceptanceTest {
    private String spaceApi;
    private Space be;
    private Space fe;
    private Long beSpaceId;
    private Map luther;

    @BeforeEach
    void setUp() {
        String lutherId = saveMap("/api/managers/maps", mapCreateUpdateRequest).header("location").split("/")[4];
        spaceApi = "/api/managers/maps/" + lutherId + "/spaces";
        ExtractableResponse<Response> saveBeSpaceResponse = saveSpace(spaceApi, beSpaceCreateUpdateRequest);
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
        luther = new Map(Long.valueOf(lutherId), LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);
        Setting beSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Setting feSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();

        be = Space.builder()
                .id(beSpaceId)
                .name(BE_NAME)
                .color(BE_COLOR)
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .spaceSettings(new Settings(List.of(beSetting)))
                .build();

        fe = Space.builder()
                .id(feSpaceId)
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .spaceSettings(new Settings(List.of(feSetting)))
                .build();
    }

    @Test
    @DisplayName("전체 공간에 대한 정보를 조회한다.")
    void findAll() {
        // given, when
        String guestSpaceApi = spaceApi.replaceAll("managers", "guests");
        ExtractableResponse<Response> response = findAllSpace(guestSpaceApi);
        SpaceFindAllResponse actual = response.body().as(SpaceFindAllResponse.class);
        SpaceFindAllResponse expected = SpaceFindAllResponse.from(List.of(be, fe));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("spaceId를 받아 해당 공간에 대한 정보를 조회한다.")
    void find() {
        // given, when
        String guestSpaceApi = spaceApi.replaceAll("managers", "guests");
        String api = guestSpaceApi + "/" + beSpaceId;
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
    @DisplayName("mapId와 함께 특정 time slot (start, end date time)이 주어지면, 맵 상의 모든 공간에 대해서 사용 가능 여부를 반환한다")
    void findAllSpaceAvailability() {
        // given, when
        String guestSpaceApi = spaceApi.replaceAll("managers", "guests");
        String api = guestSpaceApi + "/availability";
        ExtractableResponse<Response> response = findAllSpaceAvailability(api);
        SpaceFindAllAvailabilityResponse actual = response.body().as(SpaceFindAllAvailabilityResponse.class);
        SpaceFindAllAvailabilityResponse expected = SpaceFindAllAvailabilityResponse.of(
                luther.getId(), List.of(be, fe), new HashSet<>());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringActualNullFields()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    private ExtractableResponse<Response> findAllSpace(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("space/guest/getAll", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findSpace(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("space/guest/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllSpaceAvailability(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .param("startDateTime", BE_AM_TEN_ELEVEN_START_TIME_KST.toLocalDate().toString() + "T10:00:00+09:00")
                .param("endDateTime", BE_AM_TEN_ELEVEN_END_TIME_KST.toLocalDate().toString()+ "T11:00:00+09:00")
                .filter(document("space/guest/getAllAvailability", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }
}
