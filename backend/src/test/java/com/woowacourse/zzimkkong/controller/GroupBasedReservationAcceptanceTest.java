package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import com.woowacourse.zzimkkong.domain.Group;
import com.woowacourse.zzimkkong.domain.ServiceZone;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateAsManagerRequest;
import com.woowacourse.zzimkkong.dto.space.AllowedGroupRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateUpdateRequest;
import com.woowacourse.zzimkkong.infrastructure.auth.AuthorizationExtractor;
import com.woowacourse.zzimkkong.service.SlackService;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.ZoneId;
import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.controller.AuthControllerTest.login;
import static com.woowacourse.zzimkkong.controller.ManagerSpaceControllerTest.saveSpace;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("역할 기반 예약 권한 인수 테스트")
class GroupBasedReservationAcceptanceTest extends AcceptanceTest {

    @MockBean
    private SlackService slackService;

    @Test
    @DisplayName("1단계: 코치 계정과 크루 계정을 생성할 수 있다")
    void createCoachAndCrewAccounts() {
        // given & when - 코치 계정 생성
        MemberSaveRequest coachRequest = new MemberSaveRequest(
                "coach@example.com",
                "코치",
                ProfileEmoji.MAN_LIGHT_SKIN_TONE_TECHNOLOGIST,
                "password123!",
                Group.COACH
        );
        ExtractableResponse<Response> coachResponse = saveMember(coachRequest);

        // then
        assertThat(coachResponse.statusCode()).isEqualTo(201);

        // given & when - 크루 계정 생성
        MemberSaveRequest crewRequest = new MemberSaveRequest(
                "crew@example.com",
                "크루",
                ProfileEmoji.WOMAN_LIGHT_SKIN_TONE_TECHNOLOGIST,
                "password123!",
                Group.NONE
        );
        ExtractableResponse<Response> crewResponse = saveMember(crewRequest);

        // then
        assertThat(crewResponse.statusCode()).isEqualTo(201);

        // given & when - 코치 로그인
        TokenResponse coachTokenResponse = login(new LoginRequest("coach@example.com", "password123!"))
                .body().as(TokenResponse.class);

        // then
        assertThat(coachTokenResponse.getAccessToken()).isNotNull();

        // given & when - 크루 로그인
        TokenResponse crewTokenResponse = login(new LoginRequest("crew@example.com", "password123!"))
                .body().as(TokenResponse.class);

        // then
        assertThat(crewTokenResponse.getAccessToken()).isNotNull();
    }

    @Test
    @DisplayName("2단계: 일반 회의실과 코치 전용 회의실을 생성할 수 있다")
    void createNormalAndCoachOnlySpaces() {
        // given - 맵 생성
        String mapLocation = saveMap("/api/managers/maps", mapCreateUpdateRequest).header("location");
        String mapId = mapLocation.split("/")[4];
        String spaceApi = "/api/managers/maps/" + mapId + "/spaces";

        // when - 일반 회의실 생성 (allowedGroups 비어있음)
        SpaceCreateUpdateRequest normalSpaceRequest = new SpaceCreateUpdateRequest(
                "일반회의실",
                "#00FF00",
                SPACE_DRAWING,
                MAP_SVG,
                true,
                List.of(beSettingRequest),
                List.of() // 비어있음 -> 모든 사용자 가능
        );
        ExtractableResponse<Response> normalSpaceResponse = saveSpace(spaceApi, normalSpaceRequest);

        // then
        assertThat(normalSpaceResponse.statusCode()).isEqualTo(201);
        assertThat(normalSpaceResponse.header("location")).isNotNull();

        // when - 코치 전용 회의실 생성
        SpaceCreateUpdateRequest coachOnlySpaceRequest = new SpaceCreateUpdateRequest(
                "코치전용회의실",
                "#FF0000",
                SPACE_DRAWING,
                MAP_SVG,
                true,
                List.of(beSettingRequest),
                List.of(new AllowedGroupRequest(Group.COACH))
        );
        ExtractableResponse<Response> coachOnlySpaceResponse = saveSpace(spaceApi, coachOnlySpaceRequest);

        // then
        assertThat(coachOnlySpaceResponse.statusCode()).isEqualTo(201);
        assertThat(coachOnlySpaceResponse.header("location")).isNotNull();
    }

    @Test
    @DisplayName("3단계: 코치가 코치 전용 회의실에 예약할 수 있다")
    void coachCanReserveCoachOnlySpace() {
        // given - 코치 계정 생성 및 로그인
        MemberSaveRequest coachRequest = new MemberSaveRequest(
                "coach@example.com",
                "코치",
                ProfileEmoji.MAN_LIGHT_SKIN_TONE_TECHNOLOGIST,
                "password123!",
                Group.COACH
        );
        saveMember(coachRequest);
        TokenResponse coachTokenResponse = login(new LoginRequest("coach@example.com", "password123!"))
                .body().as(TokenResponse.class);
        String coachToken = coachTokenResponse.getAccessToken();

        // given - 코치 계정으로 맵 생성
        ExtractableResponse<Response> mapResponse = RestAssured
                .given().log().all()
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + coachToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mapCreateUpdateRequest)
                .when().post("/api/managers/maps")
                .then().log().all()
                .extract();

        String mapLocation = mapResponse.header("location");
        String mapId = mapLocation.split("/")[4];
        String spaceApi = "/api/managers/maps/" + mapId + "/spaces";

        // given - 코치 전용 회의실 생성
        SpaceCreateUpdateRequest coachOnlySpaceRequest = new SpaceCreateUpdateRequest(
                "코치전용회의실",
                "#FF0000",
                SPACE_DRAWING,
                MAP_SVG,
                true,
                List.of(beSettingRequest),
                List.of(new AllowedGroupRequest(Group.COACH))
        );

        ExtractableResponse<Response> coachOnlySpaceResponse = RestAssured
                .given().log().all()
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + coachToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(coachOnlySpaceRequest)
                .when().post(spaceApi)
                .then().log().all()
                .extract();

        String coachOnlySpaceReservationApi = coachOnlySpaceResponse.header("location") + "/reservations";

        // given - 예약 요청 데이터
        ReservationCreateUpdateAsManagerRequest reservationRequest = new ReservationCreateUpdateAsManagerRequest(
                THE_DAY_AFTER_TOMORROW.atTime(14, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(15, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                "코치",
                "코치 미팅",
                null,
                "coach@example.com"
        );

        // when - 코치가 예약 시도
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + coachToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationRequest)
                .when().post(coachOnlySpaceReservationApi)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("4단계: 크루가 코치 전용 회의실에 예약할 수 없다 (403 FORBIDDEN)")
    void crewCannotReserveCoachOnlySpace() {
        // given - 크루 계정 생성 및 로그인
        MemberSaveRequest crewRequest = new MemberSaveRequest(
                "crew@example.com",
                "크루",
                ProfileEmoji.WOMAN_LIGHT_SKIN_TONE_TECHNOLOGIST,
                "password123!",
                Group.NONE
        );
        saveMember(crewRequest);
        TokenResponse crewTokenResponse = login(new LoginRequest("crew@example.com", "password123!"))
                .body().as(TokenResponse.class);
        String crewToken = crewTokenResponse.getAccessToken();

        // given - 크루 계정으로 맵 생성
        ExtractableResponse<Response> mapResponse = RestAssured
                .given().log().all()
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + crewToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mapCreateUpdateRequest)
                .when().post("/api/managers/maps")
                .then().log().all()
                .extract();

        String mapLocation = mapResponse.header("location");
        String mapId = mapLocation.split("/")[4];
        String spaceApi = "/api/managers/maps/" + mapId + "/spaces";

        // given - 코치 전용 회의실 생성
        SpaceCreateUpdateRequest coachOnlySpaceRequest = new SpaceCreateUpdateRequest(
                "코치전용회의실",
                "#FF0000",
                SPACE_DRAWING,
                MAP_SVG,
                true,
                List.of(beSettingRequest),
                List.of(new AllowedGroupRequest(Group.COACH))
        );

        ExtractableResponse<Response> coachOnlySpaceResponse = RestAssured
                .given().log().all()
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + crewToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(coachOnlySpaceRequest)
                .when().post(spaceApi)
                .then().log().all()
                .extract();

        String coachOnlySpaceReservationApi = coachOnlySpaceResponse.header("location") + "/reservations";

        // given - 예약 요청 데이터
        ReservationCreateUpdateAsManagerRequest reservationRequest = new ReservationCreateUpdateAsManagerRequest(
                THE_DAY_AFTER_TOMORROW.atTime(14, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(15, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                "크루",
                "크루 미팅",
                null,
                "crew@example.com"
        );

        // when - 크루가 예약 시도
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + crewToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationRequest)
                .when().post(coachOnlySpaceReservationApi)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.body().asString()).contains("해당 공간을 예약할 수 있는 권한이 없습니다");
    }
}
