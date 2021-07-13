package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.ReservationFindAllResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationFindResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationSaveRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationDeleteRequest;
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
import java.time.LocalDateTime;
import java.util.Arrays;

import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class ReservationControllerTest extends AcceptanceTest {
    @Autowired
    private SpaceRepository spaceRepository;

    private LocalDate targetDate;
    private Space be;
    private Space fe1;
    private Reservation reservationBackEndTargetDate0To1;
    private Reservation reservationBackEndTargetDate13To14;
    private Reservation reservationBackEndTargetDate18To23;
    private Reservation reservationFrontEnd1TargetDate0to1;

    public ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(
            1L, //TODO: 나중에 인수테스트 전부 생기면 갖다 쓰기
            LocalDateTime.of(2021, 5, 6, 16, 23, 0),
            LocalDateTime.of(2021, 5, 6, 19, 23, 0),
            "1234",
            "bada",
            "회의"
    );

    @BeforeEach
    void setUp() {
        targetDate = LocalDate.of(2021, 7, 9);
        be = spaceRepository.findById(1L).orElseThrow(NoSuchSpaceException::new);
        fe1 = spaceRepository.findById(2L).orElseThrow(NoSuchSpaceException::new);

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
        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(
                1L, //TODO: 나중에 인수테스트 전부 생기면 갖다 쓰기
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                "2345",
                "sally",
                "회의입니다."
        );

        //when
        ExtractableResponse<Response> response = saveReservation(reservationSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("예약을 삭제한다.")
    @Test
    void delete() {
        //given
        saveReservation(reservationSaveRequest);
        ReservationDeleteRequest reservationDeleteRequest = new ReservationDeleteRequest("1234");

        //when
        final ExtractableResponse<Response> response = deleteReservation(reservationDeleteRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("잘못된 비밀번호로 예약삭제를 요청하면, 400 에러를 반환한다.")
    @Test
    void deleteWithWrongPassword() {
        //given
        saveReservation(reservationSaveRequest);
        ReservationDeleteRequest reservationDeleteRequest = new ReservationDeleteRequest("0987");

        //when
        final ExtractableResponse<Response> response = deleteReservation(reservationDeleteRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("map id, space id, 특정 날짜가 주어질 때 해당 맵, 해당 공간, 해당 날짜에 속하는 예약들만 찾아온다")
    @Test
    void find() {
        //given
        be = spaceRepository.findById(1L).orElseThrow(NoSuchSpaceException::new);

        //when
        ExtractableResponse<Response> response = findReservations(1L, "2021-07-09");
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
        ExtractableResponse<Response> response = findAllReservations("2021-07-09");
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

    private ExtractableResponse<Response> saveReservation(final ReservationSaveRequest reservationSaveRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationSaveRequest)
                .when().post("/api/maps/1/reservations")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteReservation(final ReservationDeleteRequest reservationDeleteRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationDeleteRequest)
                .when().delete("/api/maps/1/reservations/1")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservations(final Long spaceId, final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .filter(document("reservation/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParams(
                        "spaceId", spaceId,
                        "date", date)
                .when().get("/api/maps/1/spaces/1/reservations")
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
}
