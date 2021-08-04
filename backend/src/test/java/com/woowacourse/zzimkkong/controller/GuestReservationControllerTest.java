package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.reservation.*;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static com.woowacourse.zzimkkong.controller.SpaceControllerTest.saveSpace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class GuestReservationControllerTest extends AcceptanceTest {
    private String reservationApi;
    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest;
    private Reservation savedReservation;
    private Long beSpaceId;
    private Long feSpaceId;
    private Long savedReservationId;

    @BeforeEach
    void setUp() {
        saveMember(memberSaveRequest);
        String lutherId = saveMap("/api/managers/maps", mapCreateUpdateRequest).header("location").split("/")[4];
        String spaceApi = "/api/managers/maps/" + lutherId + "/spaces"
        ExtractableResponse<Response> saveBeSpaceResponse = saveSpace(spaceApi, beSpaceCreateUpdateRequest);
        ExtractableResponse<Response> saveFe1SpaceResponse = saveSpace(spaceApi, feSpaceCreateUpdateRequest);

        reservationApi = "/api/guests/maps/" + lutherId + "/reservations";

        beSpaceId = Long.valueOf(saveBeSpaceResponse.header("location").split("/")[6]);
        feSpaceId = Long.valueOf(saveFe1SpaceResponse.header("location").split("/")[6]);

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                beSpaceId,
                THE_DAY_AFTER_TOMORROW.atTime(1, 0),
                THE_DAY_AFTER_TOMORROW.atTime(2, 0),
                SALLY_PASSWORD,
                SALLY_NAME,
                SALLY_DESCRIPTION);

        Member pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);

        Setting beSetting = new Setting.Builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .disabledWeekdays(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Space be = new Space.Builder()
                .id(beSpaceId)
                .name(BE_NAME)
                .map(luther)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(beSetting)
                .build();

        savedReservation = new Reservation.Builder()
                .startTime(reservationCreateUpdateWithPasswordRequest.getStartDateTime())
                .endTime(reservationCreateUpdateWithPasswordRequest.getEndDateTime())
                .password(reservationCreateUpdateWithPasswordRequest.getPassword())
                .userName(reservationCreateUpdateWithPasswordRequest.getName())
                .description(reservationCreateUpdateWithPasswordRequest.getDescription())
                .space(be)
                .build();

        saveExampleReservations();
        savedReservationId = getReservationIdAfterSave(reservationCreateUpdateWithPasswordRequest);
    }

    @DisplayName("예약을 등록한다.")
    @Test
    void save() {
        //given
        ReservationCreateUpdateWithPasswordRequest newReservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                beSpaceId,
                THE_DAY_AFTER_TOMORROW.atTime(5,0),
                THE_DAY_AFTER_TOMORROW.atTime(7,0),
                SALLY_PASSWORD,
                SALLY_NAME,
                SALLY_DESCRIPTION);

        // when
        ExtractableResponse<Response> response = saveReservation(reservationApi, newReservationCreateUpdateWithPasswordRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("map id, space id, 특정 날짜가 주어질 때 해당 맵, 해당 공간, 해당 날짜에 속하는 예약들만 찾아온다")
    @Test
    void find() {
        //given, when
        String api = reservationApi.replace("/reservations", "/spaces/" + beSpaceId + "/reservations");
        ExtractableResponse<Response> response = findReservations(api, THE_DAY_AFTER_TOMORROW.toString());

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
        ExtractableResponse<Response> response = findAllReservations(reservationApi, THE_DAY_AFTER_TOMORROW.toString());

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

    @DisplayName("공간 변경 없는 새로운 예약 정보가 주어지면 예약을 업데이트 한다")
    @Test
    void update_sameSpace() {
        //given
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequestSameSpace = new ReservationCreateUpdateWithPasswordRequest(
                reservationCreateUpdateWithPasswordRequest.getSpaceId(),
                THE_DAY_AFTER_TOMORROW.atTime(1, 0),
                THE_DAY_AFTER_TOMORROW.atTime(2, 30),
                reservationCreateUpdateWithPasswordRequest.getPassword(),
                "sally",
                "회의입니다."
        );
        String api = reservationApi + "/" + savedReservationId;

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(api, reservationCreateUpdateWithPasswordRequestSameSpace);
        ExtractableResponse<Response> findResponse = findReservation(api, new ReservationPasswordAuthenticationRequest(SALLY_PASSWORD));

        ReservationResponse actualResponse = findResponse.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(
                new Reservation.Builder()
                        .id(savedReservationId)
                        .startTime(reservationCreateUpdateWithPasswordRequestSameSpace.getStartDateTime())
                        .endTime(reservationCreateUpdateWithPasswordRequestSameSpace.getEndDateTime())
                        .description(reservationCreateUpdateWithPasswordRequestSameSpace.getDescription())
                        .userName(reservationCreateUpdateWithPasswordRequestSameSpace.getName())
                        .space(BE)
                        .build());

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("공간 변경 있는 새로운 예약 정보가 주어지면 공간을 이동한 채로 예약을 업데이트 한다.")
    @Test
    void update_spaceUpdate() {
        //given
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequestDifferentSpace = new ReservationCreateUpdateWithPasswordRequest(
                FE1.getId(),
                THE_DAY_AFTER_TOMORROW.atTime(3, 30),
                THE_DAY_AFTER_TOMORROW.atTime(4, 30),
                SALLY_PASSWORD,
                "sally",
                "회의입니다."
        );

        String api = reservationApi + "/" + savedReservationId;

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(api, reservationCreateUpdateWithPasswordRequestDifferentSpace);
        ExtractableResponse<Response> findResponse = findReservations(
                api.replaceAll("/reservations/[0-9]",
                        "/spaces/" + reservationCreateUpdateWithPasswordRequestDifferentSpace.getSpaceId() + "/reservations"),
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

    @DisplayName("올바른 비밀번호와 함께 예약을 삭제한다.")
    @Test
    void delete() {
        //given, when
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest(SALLY_PASSWORD);

        String api = reservationApi + "/" + savedReservationId;

        //then
        ExtractableResponse<Response> response = deleteReservation(api, reservationPasswordAuthenticationRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("올바른 비밀번호와 함께 예약 수정을 위한 예약 조회 요청 시, 예약에 대한 정보를 반환한다")
    @Test
    void findOne() {
        //given, when
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest(SALLY_PASSWORD);

        ExtractableResponse<Response> response = findReservation(reservationApi + "/" + savedReservationId, reservationPasswordAuthenticationRequest);

        ReservationResponse actualResponse = response.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(savedReservation);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    private ExtractableResponse<Response> saveReservation(
            final String api,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/guest/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateWithPasswordRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservations(final String api, final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .filter(document("reservation/guest/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParams("date", date)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllReservations(final String api, final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .filter(document("reservation/guest/getAll", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParam("date", date)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateReservation(final String api, final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/guest/put", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateWithPasswordRequest)
                .when().put(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservation(final String api, final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/guest/postForUpdate", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationPasswordAuthenticationRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteReservation(
            final String api,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/guest/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationPasswordAuthenticationRequest)
                .when().delete(api)
                .then().log().all().extract();
    }

    private void saveExampleReservations() {
        ReservationCreateUpdateWithPasswordRequest beAmZeroOneRequest = new ReservationCreateUpdateWithPasswordRequest(
                beSpaceId,
                BE_AM_ZERO_ONE.getStartTime(),
                BE_AM_ZERO_ONE.getEndTime(),
                BE_AM_ZERO_ONE.getPassword(),
                BE_AM_ZERO_ONE.getUserName(),
                BE_AM_ZERO_ONE.getDescription());

        ReservationCreateUpdateWithPasswordRequest bePmOneTwoRequest = new ReservationCreateUpdateWithPasswordRequest(
                beSpaceId,
                BE_PM_ONE_TWO.getStartTime(),
                BE_PM_ONE_TWO.getEndTime(),
                BE_PM_ONE_TWO.getPassword(),
                BE_PM_ONE_TWO.getUserName(),
                BE_PM_ONE_TWO.getDescription());

        ReservationCreateUpdateWithPasswordRequest beNextDayPmSixTwelveRequest = new ReservationCreateUpdateWithPasswordRequest(
                beSpaceId,
                BE_NEXT_DAY_PM_SIX_TWELVE.getStartTime(),
                BE_NEXT_DAY_PM_SIX_TWELVE.getEndTime(),
                BE_NEXT_DAY_PM_SIX_TWELVE.getPassword(),
                BE_NEXT_DAY_PM_SIX_TWELVE.getUserName(),
                BE_NEXT_DAY_PM_SIX_TWELVE.getDescription());

        ReservationCreateUpdateWithPasswordRequest feZeroOneRequest = new ReservationCreateUpdateWithPasswordRequest(
                feSpaceId,
                FE1_ZERO_ONE.getStartTime(),
                FE1_ZERO_ONE.getEndTime(),
                FE1_ZERO_ONE.getPassword(),
                FE1_ZERO_ONE.getUserName(),
                FE1_ZERO_ONE.getDescription());

        BE_AM_ZERO_ONE = new Reservation.Builder()
                .id(getReservationIdAfterSave(beAmZeroOneRequest))
                .startTime(THE_DAY_AFTER_TOMORROW_START_TIME)
                .endTime(THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(1))
                .description(DESCRIPTION)
                .userName(USER_NAME)
                .password(RESERVATION_PASSWORD)
                .space(BE)
                .build();

        BE_PM_ONE_TWO = new Reservation.Builder()
                .id(getReservationIdAfterSave(bePmOneTwoRequest))
                .startTime(THE_DAY_AFTER_TOMORROW.atTime(13, 0, 0))
                .endTime(THE_DAY_AFTER_TOMORROW.atTime(14, 0, 0))
                .description("찜꽁 2차 회의")
                .userName(USER_NAME)
                .password(RESERVATION_PASSWORD)
                .space(BE)
                .build();

        BE_NEXT_DAY_PM_SIX_TWELVE = new Reservation.Builder()
                .id(getReservationIdAfterSave(beNextDayPmSixTwelveRequest))
                .startTime(THE_DAY_AFTER_TOMORROW.plusDays(1).atTime(6, 0, 0))
                .endTime(THE_DAY_AFTER_TOMORROW.plusDays(1).atTime(12, 0, 0))
                .description("찜꽁 3차 회의")
                .userName(USER_NAME)
                .password("6789")
                .space(BE)
                .build();

        FE1_ZERO_ONE = new Reservation.Builder()
                .id(getReservationIdAfterSave(feZeroOneRequest))
                .startTime(THE_DAY_AFTER_TOMORROW_START_TIME)
                .endTime(THE_DAY_AFTER_TOMORROW.atTime(1, 0, 0))
                .description("찜꽁 5차 회의")
                .userName(USER_NAME)
                .password(RESERVATION_PASSWORD)
                .space(FE1)
                .build();
    }

    private Long getReservationIdAfterSave(ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return Long.valueOf(
                saveReservation(reservationApi, reservationCreateUpdateWithPasswordRequest)
                        .header("location")
                        .split("/")[6]);
    }
}
