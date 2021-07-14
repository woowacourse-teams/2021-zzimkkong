package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.dto.ReservationDeleteRequest;
import com.woowacourse.zzimkkong.dto.ReservationFindAllResponse;
import com.woowacourse.zzimkkong.dto.ReservationFindResponse;
import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
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

import java.util.List;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static com.woowacourse.zzimkkong.controller.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class ReservationControllerTest extends AcceptanceTest {
    private static final String SALLY_PASSWORD = "1230";
    private static final String SALLY_NAME = "sally";
    private static final String SALLY_DESCRIPTION = "집 가고 싶은 회의";

    @Autowired
    private MemberRepository members;

    @Autowired
    private SpaceRepository spaces;

    @Autowired
    private MapRepository maps;

    @Autowired
    private ReservationRepository reservations;

    private ReservationSaveRequest reservationSaveRequest;
    private Reservation savedReservation;

    @BeforeEach
    void setUp() {
        members.save(POBI); //TODO: 관련 테스트메서드 생성 시 repository 안쓰도록 수정 - 샐리
        maps.save(LUTHER);
        spaces.save(BE);
        spaces.save(FE1);

        reservationSaveRequest = new ReservationSaveRequest(
                BE.getId(),
                TOMORROW_START_TIME.plusHours(1),
                TOMORROW_START_TIME.plusHours(2),
                SALLY_PASSWORD,
                SALLY_NAME,
                SALLY_DESCRIPTION);

        savedReservation = new Reservation.Builder()
                .startTime(reservationSaveRequest.getStartDateTime())
                .endTime(reservationSaveRequest.getEndDateTime())
                .password(reservationSaveRequest.getPassword())
                .userName(reservationSaveRequest.getName())
                .description(reservationSaveRequest.getDescription())
                .space(BE)
                .build();
    }

    @DisplayName("예약을 등록한다.")
    @Test
    void save() {
        //given, when
        String api = "/api/maps/" + LUTHER.getId() + "/reservations";
        ExtractableResponse<Response> response = saveReservation(api, reservationSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("예약을 삭제한다.")
    @Test
    void delete() {
        //given
        String saveApi = "/api/maps/" + LUTHER.getId() + "/reservations";
        ExtractableResponse<Response> saveResponse = saveReservation(saveApi, reservationSaveRequest);
        String api = saveResponse.header("location");

        //when
        ReservationDeleteRequest reservationDeleteRequest = new ReservationDeleteRequest("1230");

        //then
        ExtractableResponse<Response> response = deleteReservation(api, reservationDeleteRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("map id, space id, 특정 날짜가 주어질 때 해당 맵, 해당 공간, 해당 날짜에 속하는 예약들만 찾아온다")
    @Test
    void find() {
        //given
        ExtractableResponse<Response> saveResponse = saveExampleReservations();
        String spaceId = String.valueOf(reservationSaveRequest.getSpaceId());
        String api = saveResponse.header("location")
                .replaceAll("/reservations/[0-9]", "/spaces/" + spaceId + "/reservations");

        //when
        ExtractableResponse<Response> response = findReservations(api, TOMORROW.toString());

        ReservationFindResponse actualResponse = response.as(ReservationFindResponse.class);
        ReservationFindResponse expectedResponse = ReservationFindResponse.of(
                List.of(savedReservation,
                        BE_AM_ZERO_ONE,
                        BE_PM_ONE_TWO));

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
        ExtractableResponse<Response> saveResponse = saveExampleReservations();
        String api = saveResponse.header("location")
                .replaceAll("/reservations/[0-9]", "/reservations");

        //when
        ExtractableResponse<Response> response = findAllReservations(api, TOMORROW.toString());

        ReservationFindAllResponse actualResponse = response.as(ReservationFindAllResponse.class);
        ReservationFindAllResponse expectedResponse = ReservationFindAllResponse.of(
                List.of(savedReservation,
                        BE_AM_ZERO_ONE,
                        BE_PM_ONE_TWO,
                        FE1_ZERO_ONE));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    private ExtractableResponse<Response> saveExampleReservations() {
        reservations.save(BE_AM_ZERO_ONE);
        reservations.save(BE_PM_ONE_TWO);
        reservations.save(BE_NEXT_DAY_PM_SIX_TWELVE);
        reservations.save(FE1_ZERO_ONE);

        String api = "/api/maps/" + LUTHER.getId() + "/reservations";
        return saveReservation(api, reservationSaveRequest);
    }

    private ExtractableResponse<Response> saveReservation(final String api, final ReservationSaveRequest reservationSaveRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationSaveRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteReservation(final String api, final ReservationDeleteRequest reservationDeleteRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationDeleteRequest)
                .when().delete(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservations(final String api, final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .filter(document("reservation/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParams("date", date)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllReservations(final String api, final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .filter(document("reservation/get_all", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParam("date", date)
                .when().get(api)
                .then().log().all().extract();
    }
}
