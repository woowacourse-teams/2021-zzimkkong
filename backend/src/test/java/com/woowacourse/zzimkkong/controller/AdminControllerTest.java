package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.admin.MapsResponse;
import com.woowacourse.zzimkkong.dto.admin.MembersResponse;
import com.woowacourse.zzimkkong.dto.admin.PageInfo;
import com.woowacourse.zzimkkong.dto.admin.SpacesResponse;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.member.MemberFindResponse;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailWithIdResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.Constants.MAP_IMAGE_URL;
import static com.woowacourse.zzimkkong.DocumentUtils.getRequestSpecification;
import static com.woowacourse.zzimkkong.controller.ManagerSpaceControllerTest.saveSpace;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static org.assertj.core.api.Assertions.assertThat;

class AdminControllerTest extends AcceptanceTest {

    private static final Member POBI = new Member(memberSaveRequest.getEmail(), memberSaveRequest.getPassword(), memberSaveRequest.getOrganization());
    private static final Map LUTHER = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, POBI);
    private static final Setting BE_SETTING = Setting.builder()
            .availableStartTime(BE_AVAILABLE_START_TIME)
            .availableEndTime(BE_AVAILABLE_END_TIME)
            .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
            .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
            .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
            .reservationEnable(BE_RESERVATION_ENABLE)
            .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
            .build();
    private static final Space BE = Space.builder()
            .id(1L)
            .name(BE_NAME)
            .color(BE_COLOR)
            .map(LUTHER)
            .description(BE_DESCRIPTION)
            .area(SPACE_DRAWING)
            .setting(BE_SETTING)
            .build();

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
        MembersResponse membersResponse = MembersResponse.from(
                List.of(MemberFindResponse.from(POBI)),
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

    @Test
    @DisplayName("모든 맵을 조회한다.")
    void getMaps() {
        // given
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), MAP_SVG);
        saveMap("api/managers/maps", mapCreateUpdateRequest);

        // when
        ExtractableResponse<Response> response = maps();
        MapsResponse actual = response.body().as(MapsResponse.class);
        MapsResponse expected = MapsResponse.from(
                List.of(MapFindResponse.ofAdmin(LUTHER, null)),
                PageInfo.from(0, 1, 20, 1)
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("모든 공간을 조회한다.")
    void getSpaces() {
        // given
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest(
                LUTHER.getName(),
                LUTHER.getMapDrawing(),
                MAP_SVG
        );
        String lutherId = saveMap("/api/managers/maps", mapCreateUpdateRequest).header("location").split("/")[4];
        String spaceApi = "/api/managers/maps/" + lutherId + "/spaces";
        SettingsRequest settingsRequest = new SettingsRequest(
                BE_SETTING.getAvailableStartTime(),
                BE_SETTING.getAvailableEndTime(),
                BE_SETTING.getReservationTimeUnit(),
                BE_SETTING.getReservationMinimumTimeUnit(),
                BE_SETTING.getReservationMaximumTimeUnit(),
                BE_SETTING.getReservationEnable(),
                BE_SETTING.getEnabledDayOfWeek()
        );
        SpaceCreateUpdateRequest newSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                BE.getName(),
                BE.getColor(),
                BE.getDescription(),
                SPACE_DRAWING,
                settingsRequest,
                MAP_SVG
        );
        saveSpace(spaceApi, newSpaceCreateUpdateRequest);

        // when
        ExtractableResponse<Response> response = spaces();
        SpacesResponse actual = response.body().as(SpacesResponse.class);
        SpacesResponse expected = SpacesResponse.from(
                List.of(SpaceFindDetailWithIdResponse.from(BE)),
                PageInfo.from(0, 1, 20, 1)
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
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

    private ExtractableResponse<Response> maps() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().get("/admin/api/maps")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> spaces() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().get("/admin/api/spaces")
                .then().log().all().extract();
    }
}
