package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.infrastructure.AuthorizationExtractor;
import com.woowacourse.zzimkkong.service.SlackService;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.AuthControllerTest.getToken;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static com.woowacourse.zzimkkong.controller.ManagerSpaceControllerTest.saveSpace;
import static com.woowacourse.zzimkkong.service.ServiceTestFixture.THE_DAY_AFTER_TOMORROW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class ManagerReservationControllerTest extends AcceptanceTest {
    @MockBean
    private SlackService slackService;

    private Reservation savedReservation;
    private String beReservationApi;
    private String fe1ReservationApi;
    private Long savedReservationId;

    @BeforeEach
    void setUp() {
        saveMember(memberSaveRequest);
        saveMap("/api/managers/maps", mapCreateRequest);
        String spaceSaveApi = "/api/managers/maps/" + LUTHER.getId() + "/spaces";

        ExtractableResponse<Response> saveBeSpaceResponse = saveSpace(spaceSaveApi, beSpaceCreateUpdateRequest);
        ExtractableResponse<Response> saveFe1SpaceResponse = saveSpace(spaceSaveApi, feSpaceCreateUpdateRequest);

        beReservationApi = saveBeSpaceResponse.header("location") + "/reservations";
        fe1ReservationApi = saveFe1SpaceResponse.header("location") + "/reservations";
        Long beSpaceId = Long.valueOf(saveBeSpaceResponse.header("location").split("/")[6]);
        Long feSpaceId = Long.valueOf(saveFe1SpaceResponse.header("location").split("/")[6]);

        BE = new Space.Builder()
                .id(beSpaceId)
                .name(BE.getName())
                .color(BE.getColor())
                .description(BE.getDescription())
                .map(LUTHER)
                .area(SPACE_DRAWING)
                .setting(BE_SETTING)
                .build();

        FE1 = new Space.Builder()
                .id(feSpaceId)
                .name(FE1.getName())
                .color(FE1.getColor())
                .description(FE1.getDescription())
                .map(LUTHER)
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(12, 0),
                THE_DAY_AFTER_TOMORROW.atTime(13, 0),
                SALLY_PASSWORD,
                SALLY_NAME,
                SALLY_DESCRIPTION);

        saveExampleReservations();

        savedReservationId = getReservationIdAfterSave(beReservationApi, reservationCreateUpdateWithPasswordRequest);
        savedReservation = new Reservation.Builder()
                .id(savedReservationId)
                .startTime(reservationCreateUpdateWithPasswordRequest.getStartDateTime())
                .endTime(reservationCreateUpdateWithPasswordRequest.getEndDateTime())
                .password(reservationCreateUpdateWithPasswordRequest.getPassword())
                .userName(reservationCreateUpdateWithPasswordRequest.getName())
                .description(reservationCreateUpdateWithPasswordRequest.getDescription())
                .space(BE)
                .build();
    }

    @DisplayName("올바른 토큰이 주어질 때, 예약을 등록한다.")
    @Test
    void save() {
        //given
        ReservationCreateUpdateWithPasswordRequest newReservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(5),
                THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(7),
                SALLY_PASSWORD,
                SALLY_NAME,
                SALLY_DESCRIPTION);

        //when
        ExtractableResponse<Response> response = saveReservation(beReservationApi, newReservationCreateUpdateWithPasswordRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("map id, space id, 특정 날짜가 주어질 때 해당 맵, 해당 공간, 해당 날짜에 속하는 예약들만 찾아온다")
    @Test
    void find() {
        //given, when
        ExtractableResponse<Response> response = findReservations(beReservationApi, THE_DAY_AFTER_TOMORROW.toString());

        ReservationFindResponse actualResponse = response.as(ReservationFindResponse.class);
        ReservationFindResponse expectedResponse = ReservationFindResponse.from(
                Arrays.asList(savedReservation,
                        BE_AM_ZERO_ONE,
                        BE_PM_ONE_TWO));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("map id와 특정 날짜가 주어질 때 해당 맵, 해당 날짜의 모든 공간에 대한 예약을 조회한다.")
    @Test
    void findAll() {
        //given, when
        String api = beReservationApi.replaceAll("/spaces/[0-9]", "/spaces");
        ExtractableResponse<Response> response = findAllReservations(api, THE_DAY_AFTER_TOMORROW.toString());

        ReservationFindAllResponse actualResponse = response.as(ReservationFindAllResponse.class);
        ReservationFindAllResponse expectedResponse = ReservationFindAllResponse.of(
                Arrays.asList(BE, FE1),
                Arrays.asList(savedReservation,
                        BE_AM_ZERO_ONE,
                        BE_PM_ONE_TWO,
                        FE1_ZERO_ONE));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringActualNullFields()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("올바른 토큰과 함께 예약 수정을 위한 예약 조회 요청 시, 예약에 대한 정보를 반환한다")
    @Test
    void findOne() {
        //given, when
        ExtractableResponse<Response> response = findReservation(beReservationApi + "/" + savedReservationId);

        ReservationResponse actualResponse = response.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(savedReservation);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("공간 변경 없는 새로운 예약 정보가 주어지면 예약을 업데이트 한다")
    @Test
    void update_sameSpace() {
        //given
        ReservationCreateUpdateRequest reservationCreateUpdateRequestSameSpace = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(1, 0),
                THE_DAY_AFTER_TOMORROW.atTime(2, 30),
                "sally",
                "회의입니다."
        );
        String api = beReservationApi + "/" + savedReservationId;

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(api, reservationCreateUpdateRequestSameSpace);
        ExtractableResponse<Response> findResponse = findReservation(api);

        ReservationResponse actualResponse = findResponse.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(
                new Reservation.Builder()
                        .id(savedReservationId)
                        .startTime(reservationCreateUpdateRequestSameSpace.getStartDateTime())
                        .endTime(reservationCreateUpdateRequestSameSpace.getEndDateTime())
                        .description(reservationCreateUpdateRequestSameSpace.getDescription())
                        .userName(reservationCreateUpdateRequestSameSpace.getName())
                        .space(BE)
                        .build());

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("공간 변경 있는 새로운 예약 정보가 주어지면 공간을 이동한 채로 예약을 업데이트 한다.")
    @Test
    void update_spaceUpdate() {
        //given
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequestDifferentSpace = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(3, 30),
                THE_DAY_AFTER_TOMORROW.atTime(4, 30),
                SALLY_PASSWORD,
                "sally",
                "회의입니다."
        );

        String api = fe1ReservationApi + "/" + savedReservationId;

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(api, reservationCreateUpdateWithPasswordRequestDifferentSpace);
        ExtractableResponse<Response> findResponse = findReservations(
                fe1ReservationApi,
                THE_DAY_AFTER_TOMORROW.toString());

        ReservationFindResponse actualResponse = findResponse.as(ReservationFindResponse.class);
        ReservationFindResponse expectedResponse = ReservationFindResponse.from(
                Arrays.asList(
                        new Reservation.Builder()
                                .startTime(reservationCreateUpdateWithPasswordRequestDifferentSpace.getStartDateTime())
                                .endTime(reservationCreateUpdateWithPasswordRequestDifferentSpace.getEndDateTime())
                                .description(reservationCreateUpdateWithPasswordRequestDifferentSpace.getDescription())
                                .userName(reservationCreateUpdateWithPasswordRequestDifferentSpace.getName())
                                .password(reservationCreateUpdateWithPasswordRequestDifferentSpace.getPassword())
                                .space(FE1)
                                .build(),
                        FE1_ZERO_ONE
                )
        );

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("예약을 삭제한다.")
    @Test
    void delete() {
        //given, when
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest
                = new ReservationPasswordAuthenticationRequest(SALLY_PASSWORD);

        String api = beReservationApi + "/" + savedReservationId;

        //then
        ExtractableResponse<Response> response = deleteReservation(api, reservationPasswordAuthenticationRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void saveExampleReservations() {
        ReservationCreateUpdateWithPasswordRequest beAmZeroOneRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE_AM_ZERO_ONE.getStartTime(),
                BE_AM_ZERO_ONE.getEndTime(),
                BE_AM_ZERO_ONE.getPassword(),
                BE_AM_ZERO_ONE.getUserName(),
                BE_AM_ZERO_ONE.getDescription());

        ReservationCreateUpdateWithPasswordRequest bePmOneTwoRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE_PM_ONE_TWO.getStartTime(),
                BE_PM_ONE_TWO.getEndTime(),
                BE_PM_ONE_TWO.getPassword(),
                BE_PM_ONE_TWO.getUserName(),
                BE_PM_ONE_TWO.getDescription());

        ReservationCreateUpdateWithPasswordRequest beNextDayPmSixTwelveRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE_NEXT_DAY_PM_SIX_TWELVE.getStartTime(),
                BE_NEXT_DAY_PM_SIX_TWELVE.getEndTime(),
                BE_NEXT_DAY_PM_SIX_TWELVE.getPassword(),
                BE_NEXT_DAY_PM_SIX_TWELVE.getUserName(),
                BE_NEXT_DAY_PM_SIX_TWELVE.getDescription());

        ReservationCreateUpdateWithPasswordRequest feZeroOneRequest = new ReservationCreateUpdateWithPasswordRequest(
                FE1_ZERO_ONE.getStartTime(),
                FE1_ZERO_ONE.getEndTime(),
                FE1_ZERO_ONE.getPassword(),
                FE1_ZERO_ONE.getUserName(),
                FE1_ZERO_ONE.getDescription());

        BE_AM_ZERO_ONE = new Reservation.Builder()
                .id(getReservationIdAfterSave(beReservationApi, beAmZeroOneRequest))
                .startTime(BE_AM_ZERO_ONE.getStartTime())
                .endTime(BE_AM_ZERO_ONE.getEndTime())
                .description(BE_AM_ZERO_ONE.getDescription())
                .userName(BE_AM_ZERO_ONE.getUserName())
                .password(BE_AM_ZERO_ONE.getPassword())
                .space(BE)
                .build();

        BE_PM_ONE_TWO = new Reservation.Builder()
                .id(getReservationIdAfterSave(beReservationApi, bePmOneTwoRequest))
                .startTime(BE_PM_ONE_TWO.getStartTime())
                .endTime(BE_PM_ONE_TWO.getEndTime())
                .description(BE_PM_ONE_TWO.getDescription())
                .userName(BE_PM_ONE_TWO.getUserName())
                .password(BE_PM_ONE_TWO.getPassword())
                .space(BE)
                .build();

        BE_NEXT_DAY_PM_SIX_TWELVE = new Reservation.Builder()
                .id(getReservationIdAfterSave(beReservationApi, beNextDayPmSixTwelveRequest))
                .startTime(BE_NEXT_DAY_PM_SIX_TWELVE.getStartTime())
                .endTime(BE_NEXT_DAY_PM_SIX_TWELVE.getEndTime())
                .description(BE_NEXT_DAY_PM_SIX_TWELVE.getDescription())
                .userName(BE_NEXT_DAY_PM_SIX_TWELVE.getUserName())
                .password(BE_NEXT_DAY_PM_SIX_TWELVE.getPassword())
                .space(BE)
                .build();

        FE1_ZERO_ONE = new Reservation.Builder()
                .id(getReservationIdAfterSave(fe1ReservationApi, feZeroOneRequest))
                .startTime(FE1_ZERO_ONE.getStartTime())
                .endTime(FE1_ZERO_ONE.getEndTime())
                .description(FE1_ZERO_ONE.getDescription())
                .userName(FE1_ZERO_ONE.getUserName())
                .password(FE1_ZERO_ONE.getPassword())
                .space(FE1)
                .build();
    }

    private Long getReservationIdAfterSave(final String api, final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return Long.valueOf(
                saveReservation(api, reservationCreateUpdateWithPasswordRequest)
                        .header("location")
                        .split("/")[8]);
    }

    private ExtractableResponse<Response> saveReservation(
            final String api,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("reservation/manager/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateWithPasswordRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservations(final String api, final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("reservation/manager/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParams("date", date)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllReservations(final String api, final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("reservation/manager/getAll", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParam("date", date)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateReservation(final String api, final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("reservation/manager/put", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateRequest)
                .when().put(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservation(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("reservation/manager/getForUpdate", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteReservation(
            final String api,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("reservation/manager/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationPasswordAuthenticationRequest)
                .when().delete(api)
                .then().log().all().extract();
    }
}
