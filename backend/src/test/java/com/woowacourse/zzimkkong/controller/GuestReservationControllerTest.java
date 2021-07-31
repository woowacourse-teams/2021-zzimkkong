package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Reservation;
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

import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static com.woowacourse.zzimkkong.controller.SpaceControllerTest.saveSpace;
import static com.woowacourse.zzimkkong.service.ServiceTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class GuestReservationControllerTest extends AcceptanceTest {
    private final String reservationApi = "/api/guests/maps/1/reservations";
    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest;
    private Reservation savedReservation;
    private Long beSpaceId;
    private String savedReservationApi;

    @BeforeEach
    void setUp() {
        saveMember(memberSaveRequest);
        saveMap("/api/managers/maps", mapCreateRequest);
        String spaceSaveApi = "/api/managers/maps/1/spaces";
        ExtractableResponse<Response> saveBeSpaceResponse = saveSpace(spaceSaveApi, beSpaceCreateUpdateRequest);
        saveSpace(spaceSaveApi, feSpaceCreateUpdateRequest);

        beSpaceId = Long.valueOf(saveBeSpaceResponse.header("location").split("/")[6]);

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                beSpaceId,
                THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(1),
                THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(2),
                SALLY_PASSWORD,
                SALLY_NAME,
                SALLY_DESCRIPTION);

        savedReservation = new Reservation.Builder()
                .startTime(reservationCreateUpdateWithPasswordRequest.getStartDateTime())
                .endTime(reservationCreateUpdateWithPasswordRequest.getEndDateTime())
                .password(reservationCreateUpdateWithPasswordRequest.getPassword())
                .userName(reservationCreateUpdateWithPasswordRequest.getName())
                .description(reservationCreateUpdateWithPasswordRequest.getDescription())
                .space(BE)
                .build();

        ExtractableResponse<Response> saveResponse = saveExampleReservations();
        savedReservationApi = saveResponse.header("location")
                .replaceAll("/reservations/[0-9]", "/spaces/" + beSpaceId + "/reservations");
    }

    @DisplayName("예약을 등록한다.")
    @Test
    void save() {
        //given
        ReservationCreateUpdateWithPasswordRequest newReservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                beSpaceId,
                THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(5),
                THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(7),
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
        ExtractableResponse<Response> response = findReservations(savedReservationApi, THE_DAY_AFTER_TOMORROW.toString());

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
        ReservationFindAllResponse expectedResponse = ReservationFindAllResponse.from(
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
                THE_DAY_AFTER_TOMORROW.atTime(1, 0, 0),
                THE_DAY_AFTER_TOMORROW.atTime(2, 30, 0),
                reservationCreateUpdateWithPasswordRequest.getPassword(),
                "sally",
                "회의입니다."
        );
        String api = savedReservationApi.replaceAll("/spaces/[0-9]", "") + "/5";

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(api, reservationCreateUpdateWithPasswordRequestSameSpace);
        ExtractableResponse<Response> findResponse = findReservation(api, new ReservationPasswordAuthenticationRequest(SALLY_PASSWORD));

        ReservationResponse actualResponse = findResponse.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(
                new Reservation.Builder()
                        .id(5L)
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
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("공간 변경 있는 새로운 예약 정보가 주어지면 공간을 이동한 채로 예약을 업데이트 한다.")
    @Test
    void update_spaceUpdate() {
        //given
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequestDifferentSpace = new ReservationCreateUpdateWithPasswordRequest(
                2L,
                THE_DAY_AFTER_TOMORROW.atTime(3, 30, 0),
                THE_DAY_AFTER_TOMORROW.atTime(4, 30, 0),
                SALLY_PASSWORD,
                "sally",
                "회의입니다."
        );

        String api = savedReservationApi.replaceAll("/spaces/[0-9]", "") + "/5";

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

        String api = savedReservationApi.replaceAll("/spaces/[0-9]", "") + "/5";

        //then
        ExtractableResponse<Response> response = deleteReservation(api, reservationPasswordAuthenticationRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("올바른 비밀번호와 함께 예약 수정을 위한 예약 조회 요청 시, 예약에 대한 정보를 반환한다")
    @Test
    void findOne() {
        // given, when
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest(SALLY_PASSWORD);

        ExtractableResponse<Response> response = findReservation(reservationApi + "/5", reservationPasswordAuthenticationRequest);

        ReservationResponse actualResponse = response.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(savedReservation);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    private ExtractableResponse<Response> saveExampleReservations() {
        ReservationCreateUpdateWithPasswordRequest beAmZeroOneRequest = new ReservationCreateUpdateWithPasswordRequest(
                1L,
                THE_DAY_AFTER_TOMORROW_START_TIME,
                THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(1),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION);

        ReservationCreateUpdateWithPasswordRequest bePmOneTwoRequest = new ReservationCreateUpdateWithPasswordRequest(
                1L,
                THE_DAY_AFTER_TOMORROW.atTime(13, 0),
                THE_DAY_AFTER_TOMORROW.atTime(14, 0),
                RESERVATION_PASSWORD,
                USER_NAME,
                "찜꽁 2차 회의");

        ReservationCreateUpdateWithPasswordRequest beNextDayPmSixTwelveRequest = new ReservationCreateUpdateWithPasswordRequest(
                1L,
                THE_DAY_AFTER_TOMORROW.plusDays(1).atTime(6, 0),
                THE_DAY_AFTER_TOMORROW.plusDays(1).atTime(12, 0),
                "6789",
                USER_NAME,
                "찜꽁 3차 회의");

        ReservationCreateUpdateWithPasswordRequest feZeroOneRequest = new ReservationCreateUpdateWithPasswordRequest(
                2L,
                THE_DAY_AFTER_TOMORROW.atStartOfDay(),
                THE_DAY_AFTER_TOMORROW.atTime(1, 0),
                RESERVATION_PASSWORD,
                USER_NAME,
                "찜꽁 5차 회의");

        saveReservation(reservationApi, beAmZeroOneRequest);
        saveReservation(reservationApi, bePmOneTwoRequest);
        saveReservation(reservationApi, beNextDayPmSixTwelveRequest);
        saveReservation(reservationApi, feZeroOneRequest);

        return saveReservation(reservationApi, this.reservationCreateUpdateWithPasswordRequest);
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
                // TODO: getAll로 고치기
                .filter(document("reservation/guest/get_all", getRequestPreprocessor(), getResponsePreprocessor()))
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
                .filter(document("reservation/guest/post_for_update", getRequestPreprocessor(), getResponsePreprocessor()))
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
}
