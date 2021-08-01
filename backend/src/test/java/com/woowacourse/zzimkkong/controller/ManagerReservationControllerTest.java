package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.infrastructure.AuthorizationExtractor;
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

import java.util.Arrays;
import java.util.List;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.AuthControllerTest.getToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class ManagerReservationControllerTest extends AcceptanceTest {
    @Autowired
    private MemberRepository members;

    @Autowired
    private SpaceRepository spaces;

    @Autowired
    private MapRepository maps;

    @Autowired
    private ReservationRepository reservations;

    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest;
    private Reservation savedReservation;
    private String token;

    @BeforeEach
    void setUp() {
        members.save(POBI); //TODO: 관련 테스트메서드 생성 시 repository 안쓰도록 수정 - 샐리
        maps.save(LUTHER);
        spaces.save(BE);
        spaces.save(FE1);

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE.getId(),
                TOMORROW.atTime(12,0),
                TOMORROW.atTime(13,0),
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

        token = getToken();
    }

    @DisplayName("올바른 토큰이 주어질 때, 예약을 등록한다.")
    @Test
    void save() {
        //given, when
        String api = "/api/managers/maps/" + LUTHER.getId() + "/reservations";
        ExtractableResponse<Response> response = saveReservation(token, api, reservationCreateUpdateWithPasswordRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("올바른 토큰이 주어질 때, 예약을 삭제한다.")
    @Test
    void delete() {
        //given
        String saveApi = "/api/managers/maps/" + LUTHER.getId() + "/reservations";
        ExtractableResponse<Response> saveResponse = saveReservation(token, saveApi, reservationCreateUpdateWithPasswordRequest);
        String api = saveResponse.header("location");

        //when
        ExtractableResponse<Response> response = deleteReservation(token, api);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("map id, space id, 특정 날짜가 주어질 때 해당 맵, 해당 공간, 해당 날짜에 속하는 예약들만 찾아온다")
    @Test
    void find() {
        //given
        ExtractableResponse<Response> saveResponse = saveExampleReservations();
        String spaceId = String.valueOf(reservationCreateUpdateWithPasswordRequest.getSpaceId());
        String api = saveResponse.header("location")
                .replaceAll("/reservations/[0-9]", "/spaces/" + spaceId + "/reservations");

        //when
        ExtractableResponse<Response> response = findReservations(token, api, TOMORROW.toString());

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
        //given
        ExtractableResponse<Response> saveResponse = saveExampleReservations();
        String api = saveResponse.header("location")
                .replaceAll("/reservations/[0-9]", "/reservations");

        //when
        ExtractableResponse<Response> response = findAllReservations(token, api, TOMORROW.toString());

        ReservationFindAllResponse actualResponse = response.as(ReservationFindAllResponse.class);
        ReservationFindAllResponse expectedResponse = ReservationFindAllResponse.from(
                List.of(BE, FE1),
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

    @DisplayName("공간 변경 없는 새로운 예약 정보가 주어지면 예약을 업데이트 한다")
    @Test
    void update_sameSpace() {
        //given
        ExtractableResponse<Response> saveResponse = saveExampleReservations();
        String api = saveResponse.header("location");

        ReservationCreateUpdateRequest reservationCreateUpdateRequestSameSpace = new ReservationCreateUpdateRequest(
                reservationCreateUpdateWithPasswordRequest.getSpaceId(),
                TOMORROW.atTime(1, 0, 0),
                TOMORROW.atTime(2, 30, 0),
                "sally",
                "회의입니다."
        );

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(token, api, reservationCreateUpdateRequestSameSpace);
        ExtractableResponse<Response> findResponse = findReservation(token, api);

        ReservationResponse actualResponse = findResponse.body().as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(new Reservation.Builder()
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

    @DisplayName("공간 변경 있는 새로운 예약 정보가 주어지면 공간을 이동한 채로 예약을 업데이트 한다")
    @Test
    void update_spaceUpdate() {
        //given
        ExtractableResponse<Response> saveResponse = saveExampleReservations();
        String api = saveResponse.header("location");

        ReservationCreateUpdateRequest reservationCreateUpdateRequestDifferentSpace = new ReservationCreateUpdateRequest(
                FE1.getId(),
                TOMORROW.atTime(3, 30, 0),
                TOMORROW.atTime(4, 30, 0),
                "sally",
                "회의입니다."
        );

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(token, api, reservationCreateUpdateRequestDifferentSpace);
        ExtractableResponse<Response> findResponse = findReservations(
                token,
                api.replaceAll("/reservations/[0-9]",
                        "/spaces/" + FE1.getId() + "/reservations"),
                TOMORROW.toString());

        ReservationFindResponse actualResponse = findResponse.as(ReservationFindResponse.class);
        ReservationFindResponse expectedResponse = ReservationFindResponse.from(
                Arrays.asList(
                        new Reservation.Builder()
                                .startTime(reservationCreateUpdateRequestDifferentSpace.getStartDateTime())
                                .endTime(reservationCreateUpdateRequestDifferentSpace.getEndDateTime())
                                .description(reservationCreateUpdateRequestDifferentSpace.getDescription())
                                .userName(reservationCreateUpdateRequestDifferentSpace.getName())
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

    @DisplayName("올바른 토큰과 함께 예약 수정을 위한 예약 조회 요청 시, 예약에 대한 정보를 반환한다")
    @Test
    void findOne() {
        //given
        ExtractableResponse<Response> saveResponse = saveExampleReservations();
        String api = saveResponse.header("location");

        //when
        ExtractableResponse<Response> response = findReservation(token, api);

        ReservationResponse actualResponse = response.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(savedReservation);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    private ExtractableResponse<Response> saveExampleReservations() {
        reservations.save(BE_AM_ZERO_ONE);
        reservations.save(BE_PM_ONE_TWO);
        reservations.save(BE_NEXT_DAY_PM_SIX_TWELVE);
        reservations.save(FE1_ZERO_ONE);

        String api = "/api/managers/maps/" + LUTHER.getId() + "/reservations";
        return saveReservation(token, api, reservationCreateUpdateWithPasswordRequest);
    }

    private ExtractableResponse<Response> saveReservation(
            final String token,
            final String api,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + token)
                .filter(document("reservation/manager/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateWithPasswordRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteReservation(
            final String token,
            final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + token)
                .filter(document("reservation/manager/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .when().delete(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllReservations(
            final String token,
            final String api,
            final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + token)
                .filter(document("reservation/manager/get_all", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParam("date", date)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservations(
            final String token,
            final String api,
            final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + token)
                .filter(document("reservation/manager/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParams("date", date)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservation(
            final String token,
            final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + token)
                .filter(document("reservation/manager/get_one", getRequestPreprocessor(), getResponsePreprocessor()))
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateReservation(
            final String token,
            final String api,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + token)
                .filter(document("reservation/manager/put", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateRequest)
                .when().put(api)
                .then().log().all().extract();
    }
}
