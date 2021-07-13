package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.dto.Validator.DATE_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class ReservationControllerTest extends AcceptanceTest {
    @Autowired
    private SpaceRepository spaceRepository;

    private LocalDate targetDate;
    private String targetDateString;
    private Space be;
    private Space fe1;
    private Reservation reservationBackEndTargetDate0To1;
    private Reservation reservationBackEndTargetDate13To14;
    private Reservation reservationBackEndTargetDate18To23;
    private Reservation reservationFrontEnd1TargetDate0to1;
    private ReservationCreateUpdateRequest reservationCreateUpdateRequestSameSpace;
    private ReservationCreateUpdateRequest reservationCreateUpdateRequestDifferentSpace;
    private ReservationCreateUpdateRequest reservationCreateUpdateRequestForFail;

    @BeforeEach
    void setUp() {
        targetDate = LocalDate.now().plusDays(1L);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        targetDateString = targetDate.format(formatter);

        be = spaceRepository.findById(1L).orElseThrow(NoSuchSpaceException::new);
        fe1 = spaceRepository.findById(2L).orElseThrow(NoSuchSpaceException::new);

        reservationCreateUpdateRequestSameSpace = new ReservationCreateUpdateRequest(
                1L,
                targetDate.atTime(1, 0, 0),
                targetDate.atTime(2, 30, 0),
                "1234",
                "sally",
                "회의입니다."
        );

        reservationCreateUpdateRequestDifferentSpace = new ReservationCreateUpdateRequest(
                2L,
                targetDate.atTime(3, 30, 0),
                targetDate.atTime(4, 30, 0),
                "1234",
                "sally",
                "회의입니다."
        );

        reservationCreateUpdateRequestForFail = new ReservationCreateUpdateRequest(
                1L,
                targetDate.atTime(12, 0, 0),
                targetDate.atTime(13, 30, 0),
                "1234",
                "sally",
                "회의입니다."
        );

        reservationBackEndTargetDate0To1 = new Reservation.Builder()
                .startTime(targetDate.atStartOfDay())
                .endTime(targetDate.atTime(1, 0, 0))
                .description("찜꽁 1차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(be)
                .build();

        reservationBackEndTargetDate13To14 = new Reservation.Builder()
                .startTime(targetDate.atTime(13, 0, 0))
                .endTime(targetDate.atTime(14, 0, 0))
                .description("찜꽁 2차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(be)
                .build();

        reservationBackEndTargetDate18To23 = new Reservation.Builder()
                .startTime(targetDate.atTime(18, 0, 0))
                .endTime(targetDate.atTime(23, 59, 59))
                .description("찜꽁 3차 회의")
                .userName("찜꽁")
                .password("6789")
                .space(be)
                .build();

        reservationFrontEnd1TargetDate0to1 = new Reservation.Builder()
                .startTime(targetDate.atStartOfDay())
                .endTime(targetDate.atTime(1, 0, 0))
                .description("찜꽁 5차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(fe1)
                .build();
    }

    @DisplayName("예약을 등록한다.")
    @Test
    void save() {
        //given

        //when
        ExtractableResponse<Response> response = saveReservation(reservationCreateUpdateRequestSameSpace);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("예약을 삭제한다.")
    @Test
    void delete() {
        //given
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest("1234");

        //when
        final ExtractableResponse<Response> response = deleteReservation(reservationPasswordAuthenticationRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("잘못된 비밀번호로 예약삭제를 요청하면, 400 에러를 반환한다.")
    @Test
    void deleteWithWrongPassword() {
        //given
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest("0987");

        //when
        final ExtractableResponse<Response> response = deleteReservation(reservationPasswordAuthenticationRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("map id, space id, 특정 날짜가 주어질 때 해당 맵, 해당 공간, 해당 날짜에 속하는 예약들만 찾아온다")
    @Test
    void find() {
        //given
        be = spaceRepository.findById(1L).orElseThrow(NoSuchSpaceException::new);

        //when
        ExtractableResponse<Response> response = findReservations(1L, targetDateString);
        ReservationFindResponse actualResponse = response.as(ReservationFindResponse.class);

        ReservationFindResponse expectedResponse = ReservationFindResponse.of(
                Arrays.asList(
                        reservationBackEndTargetDate0To1,
                        reservationBackEndTargetDate13To14,
                        reservationBackEndTargetDate18To23
                )
        );

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("map id와 특정 날짜가 주어질 때 해당 맵, 해당 날짜의 모든 공간에 대한 예약 조회")
    @Test
    void findAll() {
        //given

        //when
        ExtractableResponse<Response> response = findAllReservations(targetDateString);
        ReservationFindAllResponse actualResponse = response.as(ReservationFindAllResponse.class);

        ReservationFindAllResponse expectedResponse = ReservationFindAllResponse.of(
                Arrays.asList(
                        reservationBackEndTargetDate0To1,
                        reservationBackEndTargetDate13To14,
                        reservationBackEndTargetDate18To23,
                        reservationFrontEnd1TargetDate0to1
                )
        );

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("공간 변경 없는 새로운 예약 정보가 주어지면 예약을 업데이트 한다")
    @Test
    void update_sameSpace() {
        //given

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(reservationCreateUpdateRequestSameSpace);
        ExtractableResponse<Response> findResponse = findReservations(
                be.getId(),
                targetDateString);
        ReservationFindResponse actualResponse = findResponse.as(ReservationFindResponse.class);

        ReservationFindResponse expectedResponse = ReservationFindResponse.of(
                Arrays.asList(
                        new Reservation.Builder()
                                .startTime(reservationCreateUpdateRequestSameSpace.getStartDateTime())
                                .endTime(reservationCreateUpdateRequestSameSpace.getEndDateTime())
                                .description(reservationCreateUpdateRequestSameSpace.getDescription())
                                .userName(reservationCreateUpdateRequestSameSpace.getName())
                                .password(reservationCreateUpdateRequestSameSpace.getPassword())
                                .space(be)
                                .build(),
                        reservationBackEndTargetDate13To14,
                        reservationBackEndTargetDate18To23
                )
        );

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("공간 변경 있는 새로운 예약 정보가 주어지면 공간을 이동한 채로 예약을 업데이트 한다")
    @Test
    void update_spaceUpdate() {
        //given

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(reservationCreateUpdateRequestDifferentSpace);
        ExtractableResponse<Response> findResponse = findReservations(
                fe1.getId(),
                targetDateString);
        ReservationFindResponse actualResponse = findResponse.as(ReservationFindResponse.class);

        ReservationFindResponse expectedResponse = ReservationFindResponse.of(
                Arrays.asList(
                        new Reservation.Builder()
                                .startTime(reservationCreateUpdateRequestDifferentSpace.getStartDateTime())
                                .endTime(reservationCreateUpdateRequestDifferentSpace.getEndDateTime())
                                .description(reservationCreateUpdateRequestDifferentSpace.getDescription())
                                .userName(reservationCreateUpdateRequestDifferentSpace.getName())
                                .password(reservationCreateUpdateRequestDifferentSpace.getPassword())
                                .space(fe1)
                                .build(),
                        reservationFrontEnd1TargetDate0to1
                )
        );

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("예약 할 수 없는 조건의 정보가 주어지면 400 에러를 반환한다")
    @Test
    void update_fail() {
        //given

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(reservationCreateUpdateRequestForFail);

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("정확한 예약 비밀번호를 입력하면 해당 예약에 대한 정보를 반환한다")
    @Test
    void findOne() {
        //given
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest("1234");

        //when
        ExtractableResponse<Response> response = findReservation(reservationPasswordAuthenticationRequest);
        ReservationResponse actualResponse = response.as(ReservationResponse.class);

        ReservationResponse expectedResponse = ReservationResponse.of(
                reservationBackEndTargetDate0To1
        );

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    private ExtractableResponse<Response> saveReservation(final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateRequest)
                .when().post("/api/maps/1/reservations")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteReservation(final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationPasswordAuthenticationRequest)
                .when().delete("/api/maps/1/reservations/1")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservations(final Long spaceId, final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .filter(document("reservation/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParam("date", date)
                .when().get("/api/maps/1/spaces/" + spaceId + "/reservations")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllReservations(final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .filter(document("reservation/get_all", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParam("date", date)
                .when().get("/api/maps/1/reservations")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateReservation(final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/put", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateRequest)
                .when().put("/api/maps/1/reservations/1")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservation(final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/post_for_update", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationPasswordAuthenticationRequest)
                .when().post("/api/maps/1/reservations/1")
                .then().log().all().extract();
    }
}
