package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.admin.*;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.MemberFindResponse;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateWithPasswordRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailWithIdResponse;
import com.woowacourse.zzimkkong.infrastructure.auth.AuthorizationExtractor;
import com.woowacourse.zzimkkong.service.AdminService;
import com.woowacourse.zzimkkong.service.SlackService;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.ZoneId;
import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.getRequestSpecification;
import static com.woowacourse.zzimkkong.controller.ManagerReservationControllerTest.saveNonLoginReservation;
import static com.woowacourse.zzimkkong.controller.ManagerSpaceControllerTest.saveSpace;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AdminControllerTest extends AcceptanceTest {
    @MockBean
    private SlackService slackService;

    private static final Member POBI = Member.builder()
            .email(memberSaveRequest.getEmail())
            .userName(memberSaveRequest.getUserName())
            .emoji(memberSaveRequest.getEmoji())
            .password(memberSaveRequest.getPassword())
            .build();

    private static final Map LUTHER = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, POBI);
    private static final Setting BE_SETTING = Setting.builder()
            .settingTimeSlot(TimeSlot.of(
                    BE_AVAILABLE_START_TIME,
                    BE_AVAILABLE_END_TIME))
            .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
            .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
            .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
            .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
            .build();
    private static final Space BE = Space.builder()
            .name(BE_NAME)
            .color(BE_COLOR)
            .map(LUTHER)
            .area(SPACE_DRAWING)
            .reservationEnable(BE_RESERVATION_ENABLE)
            .spaceSettings(new Settings(List.of(BE_SETTING)))
            .build();

    private static String token;

    @BeforeEach
    void setUp() {
        postAdminLogin();
    }

    @Test
    @DisplayName("올바른 로그인이 들어오면 200 ok를 반환한다.")
    void postAdminLogin() {
        // given, when
        LoginRequest adminLoginRequest = new LoginRequest("asdf", "asdf");
        ExtractableResponse<Response> response = login(adminLoginRequest);
        TokenResponse tokenResponse = response.as(TokenResponse.class);
        token = tokenResponse.getAccessToken();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("모든 회원을 조회한다.")
    void getMembers() {
        // given
        MembersResponse membersResponse = MembersResponse.of(
                List.of(MemberFindResponse.from(POBI)),
                PageInfo.of(0, 1, 20, 1)
        );

        // when
        ExtractableResponse<Response> response = get("/admin/api/members");
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
        saveMap("api/managers/maps", mapCreateUpdateRequest);

        // when
        ExtractableResponse<Response> response = get("/admin/api/maps");
        MapsResponse actual = response.body().as(MapsResponse.class);
        MapsResponse expected = MapsResponse.of(
                List.of(MapFindResponse.ofAdmin(LUTHER, null)),
                PageInfo.of(0, 1, 20, 1)
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
        String lutherId = saveMap("/api/managers/maps", mapCreateUpdateRequest).header("location").split("/")[4];
        String spaceApi = "/api/managers/maps/" + lutherId + "/spaces";
        saveSpace(spaceApi, beSpaceCreateUpdateRequest);

        // when
        ExtractableResponse<Response> response = get("/admin/api/spaces");
        SpacesResponse actual = response.body().as(SpacesResponse.class);
        SpacesResponse expected = SpacesResponse.of(
                List.of(SpaceFindDetailWithIdResponse.fromAdmin(BE)),
                PageInfo.of(0, 1, 20, 1)
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("모든 예약을 조회한다.")
    void getReservations() {
        // given
        String lutherId = saveMap("/api/managers/maps", mapCreateUpdateRequest).header("location").split("/")[4];
        String spaceApi = "/api/managers/maps/" + lutherId + "/spaces";
        ExtractableResponse<Response> saveBeSpaceResponse = saveSpace(spaceApi, beSpaceCreateUpdateRequest);
        String beReservationApi = saveBeSpaceResponse.header("location") + "/reservations";
        ReservationCreateUpdateWithPasswordRequest newReservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                SALLY_PW,
                SALLY_NAME,
                SALLY_DESCRIPTION);
        saveNonLoginReservation(beReservationApi, newReservationCreateUpdateWithPasswordRequest);
        Reservation reservation = Reservation.builder()
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                newReservationCreateUpdateWithPasswordRequest.localStartDateTime(),
                                newReservationCreateUpdateWithPasswordRequest.localEndDateTime()))
                .userName(newReservationCreateUpdateWithPasswordRequest.getName())
                .password(newReservationCreateUpdateWithPasswordRequest.getPassword())
                .description(newReservationCreateUpdateWithPasswordRequest.getDescription())
                .space(BE)
                .build();

        // when
        ExtractableResponse<Response> response = get("/admin/api/reservations");
        ReservationsResponse actual = response.body().as(ReservationsResponse.class);
        ReservationsResponse expected = ReservationsResponse.of(
                List.of(ReservationResponse.fromAdmin(reservation)),
                PageInfo.of(0, 1, 20, 1)
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("test로 동작 시 url이 존재하지 않아 400 에러가 발생한다..")
    void getProfile() {
        // given, when
        ExtractableResponse<Response> response = get("/admin/api/profile");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @DisplayName("dev, prod로 동작 시 해당 profile을 조회해 알맞는 url을 반환한다..")
    @CsvSource(value = {"dev:200:dev.zzimkkong.com", "prod:200:zzimkkong.com"}, delimiter = ':')
    void getOtherProfile(String profile, int status, String url) {
        //given
        AdminService adminService = mock(AdminService.class);
        AdminController adminController = new AdminController(adminService, profile);

        //when
        ResponseEntity<String> response = adminController.profile();

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.valueOf(status));
        assertThat(response.getBody()).isEqualTo(url);
    }

    static ExtractableResponse<Response> login(LoginRequest adminLoginRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(adminLoginRequest)
                .when().post("/admin/api/login")
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> get(String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + token)
                .when().get(api)
                .then().log().all().extract();
    }
}
