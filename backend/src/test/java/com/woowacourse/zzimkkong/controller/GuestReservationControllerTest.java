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
import static com.woowacourse.zzimkkong.controller.ManagerSpaceControllerTest.saveSpace;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class GuestReservationControllerTest extends AcceptanceTest {
    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest;
    private Reservation savedReservation;
    private String beReservationApi;
    private String fe1ReservationApi;
    private Long savedReservationId;

    private Space be;
    private Space fe;

    private Reservation beAmZeroOne;
    private Reservation bePmOneTwo;
    private Reservation fe1ZeroOne;

    @BeforeEach
    void setUp() {
        String lutherId = saveMap("/api/members/maps", mapCreateUpdateRequest).header("location").split("/")[4];
        String spaceApi = "/api/members/maps/" + lutherId + "/spaces";
        ExtractableResponse<Response> saveBeSpaceResponse = saveSpace(spaceApi, beSpaceCreateUpdateRequest);
        ExtractableResponse<Response> saveFe1SpaceResponse = saveSpace(spaceApi, feSpaceCreateUpdateRequest);

        Long beSpaceId = Long.valueOf(saveBeSpaceResponse.header("location").split("/")[6]);
        Long feSpaceId = Long.valueOf(saveFe1SpaceResponse.header("location").split("/")[6]);

        beReservationApi = saveBeSpaceResponse.header("location")
                .replaceAll("members", "guests") + "/reservations";
        fe1ReservationApi = saveFe1SpaceResponse.header("location")
                .replaceAll("members", "guests") + "/reservations";

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(15, 0),
                THE_DAY_AFTER_TOMORROW.atTime(16, 0),
                SALLY_PW,
                SALLY_NAME,
                SALLY_DESCRIPTION);

        Member pobi = new Member(EMAIL, passwordEncoder.encode(PW), ORGANIZATION);
        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);

        Setting beSetting = Setting.builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        be = Space.builder()
                .id(beSpaceId)
                .name(BE_NAME)
                .map(luther)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(beSetting)
                .build();

        Setting feSetting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();

        fe = Space.builder()
                .id(feSpaceId)
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .description(FE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(feSetting)
                .build();

        saveExampleReservations();
        savedReservationId = getReservationIdAfterSave(beReservationApi, reservationCreateUpdateWithPasswordRequest);
        savedReservation = Reservation.builder()
                .startTime(reservationCreateUpdateWithPasswordRequest.getStartDateTime())
                .endTime(reservationCreateUpdateWithPasswordRequest.getEndDateTime())
                .password(reservationCreateUpdateWithPasswordRequest.getPassword())
                .userName(reservationCreateUpdateWithPasswordRequest.getName())
                .description(reservationCreateUpdateWithPasswordRequest.getDescription())
                .space(be)
                .build();
    }

    @Test
    @DisplayName("예약을 등록한다.")
    void save() {
        //given
        ReservationCreateUpdateWithPasswordRequest newReservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 0),
                THE_DAY_AFTER_TOMORROW.atTime(20, 0),
                SALLY_PW,
                SALLY_NAME,
                SALLY_DESCRIPTION);

        // when
        ExtractableResponse<Response> response = saveReservation(beReservationApi, newReservationCreateUpdateWithPasswordRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("map id, space id, 특정 날짜가 주어질 때 해당 맵, 해당 공간, 해당 날짜에 속하는 예약들만 찾아온다")
    void find() {
        //given, when
        ExtractableResponse<Response> response = findReservations(beReservationApi, THE_DAY_AFTER_TOMORROW.toString());

        ReservationFindResponse actualResponse = response.as(ReservationFindResponse.class);
        ReservationFindResponse expectedResponse = ReservationFindResponse.from(
                Arrays.asList(savedReservation,
                        beAmZeroOne,
                        bePmOneTwo));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("map id와 특정 날짜가 주어질 때 해당 맵, 해당 날짜의 모든 공간에 대한 예약을 조회한다.")
    void findAll() {
        //given, when
        String api = beReservationApi.replaceAll("/spaces/[0-9]", "/spaces");
        ExtractableResponse<Response> response = findAllReservations(api, THE_DAY_AFTER_TOMORROW.toString());

        ReservationFindAllResponse actualResponse = response.as(ReservationFindAllResponse.class);
        ReservationFindAllResponse expectedResponse = ReservationFindAllResponse.of(
                Arrays.asList(be, fe),
                Arrays.asList(savedReservation,
                        beAmZeroOne,
                        bePmOneTwo,
                        fe1ZeroOne));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringActualNullFields()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("공간 변경 없는 새로운 예약 정보가 주어지면 예약을 업데이트 한다")
    void update_sameSpace() {
        //given
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequestSameSpace = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 0),
                THE_DAY_AFTER_TOMORROW.atTime(20, 30),
                reservationCreateUpdateWithPasswordRequest.getPassword(),
                "sally",
                "회의입니다."
        );
        String api = beReservationApi + "/" + savedReservationId;

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(api, reservationCreateUpdateWithPasswordRequestSameSpace);
        ExtractableResponse<Response> findResponse = findReservation(api, new ReservationPasswordAuthenticationRequest(SALLY_PW));

        ReservationResponse actualResponse = findResponse.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(
                Reservation.builder()
                        .id(savedReservationId)
                        .startTime(reservationCreateUpdateWithPasswordRequestSameSpace.getStartDateTime())
                        .endTime(reservationCreateUpdateWithPasswordRequestSameSpace.getEndDateTime())
                        .description(reservationCreateUpdateWithPasswordRequestSameSpace.getDescription())
                        .userName(reservationCreateUpdateWithPasswordRequestSameSpace.getName())
                        .space(be)
                        .build());

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("공간 변경 있는 새로운 예약 정보가 주어지면 공간을 이동한 채로 예약을 업데이트 한다.")
    void update_spaceUpdate() {
        //given
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequestDifferentSpace = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 30),
                THE_DAY_AFTER_TOMORROW.atTime(20, 30),
                SALLY_PW,
                "sally",
                "회의입니다."
        );

        String api = fe1ReservationApi + "/" + savedReservationId;

        //when
        ExtractableResponse<Response> updateResponse = updateReservation(api, reservationCreateUpdateWithPasswordRequestDifferentSpace);
        ExtractableResponse<Response> findResponse = findReservations(fe1ReservationApi, THE_DAY_AFTER_TOMORROW.toString());

        ReservationFindResponse actualResponse = findResponse.as(ReservationFindResponse.class);
        ReservationFindResponse expectedResponse = ReservationFindResponse.from(
                Arrays.asList(
                        Reservation.builder()
                                .startTime(reservationCreateUpdateWithPasswordRequestDifferentSpace.getStartDateTime())
                                .endTime(reservationCreateUpdateWithPasswordRequestDifferentSpace.getEndDateTime())
                                .description(reservationCreateUpdateWithPasswordRequestDifferentSpace.getDescription())
                                .userName(reservationCreateUpdateWithPasswordRequestDifferentSpace.getName())
                                .password(reservationCreateUpdateWithPasswordRequestDifferentSpace.getPassword())
                                .space(fe)
                                .build(),
                        fe1ZeroOne
                )
        );

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("올바른 비밀번호와 함께 예약을 삭제한다.")
    void delete() {
        //given, when
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest(SALLY_PW);
        String api = beReservationApi + "/" + savedReservationId;

        //then
        ExtractableResponse<Response> response = deleteReservation(api, reservationPasswordAuthenticationRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("올바른 비밀번호와 함께 예약 수정을 위한 예약 조회 요청 시, 예약에 대한 정보를 반환한다")
    void findOne() {
        //given, when
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest(SALLY_PW);
        ExtractableResponse<Response> response = findReservation(beReservationApi + "/" + savedReservationId, reservationPasswordAuthenticationRequest);

        ReservationResponse actualResponse = response.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(savedReservation);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    private void saveExampleReservations() {
        ReservationCreateUpdateWithPasswordRequest beAmZeroOneRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE_AM_TEN_ELEVEN_START_TIME,
                BE_AM_TEN_ELEVEN_END_TIME,
                BE_AM_TEN_ELEVEN_PW,
                BE_AM_TEN_ELEVEN_USERNAME,
                BE_AM_TEN_ELEVEN_DESCRIPTION);

        ReservationCreateUpdateWithPasswordRequest bePmOneTwoRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE_PM_ONE_TWO_START_TIME,
                BE_PM_ONE_TWO_END_TIME,
                BE_PM_ONE_TWO_PW,
                BE_PM_ONE_TWO_USERNAME,
                BE_PM_ONE_TWO_DESCRIPTION);

        ReservationCreateUpdateWithPasswordRequest beNextDayAmSixTwelveRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE_NEXT_DAY_PM_FOUR_TO_SIX_START_TIME,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_END_TIME,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_PW,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_USERNAME,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_DESCRIPTION);

        ReservationCreateUpdateWithPasswordRequest feZeroOneRequest = new ReservationCreateUpdateWithPasswordRequest(
                FE1_AM_TEN_ELEVEN_START_TIME,
                FE1_AM_TEN_ELEVEN_END_TIME,
                FE1_AM_TEN_ELEVEN_PW,
                FE1_AM_TEN_ELEVEN_USERNAME,
                FE1_AM_TEN_ELEVEN_DESCRIPTION);

        beAmZeroOne = Reservation.builder()
                .id(getReservationIdAfterSave(beReservationApi, beAmZeroOneRequest))
                .startTime(BE_AM_TEN_ELEVEN_START_TIME)
                .endTime(BE_AM_TEN_ELEVEN_END_TIME)
                .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(BE_AM_TEN_ELEVEN_USERNAME)
                .password(BE_AM_TEN_ELEVEN_PW)
                .space(be)
                .build();

        bePmOneTwo = Reservation.builder()
                .id(getReservationIdAfterSave(beReservationApi, bePmOneTwoRequest))
                .startTime(BE_PM_ONE_TWO_START_TIME)
                .endTime(BE_PM_ONE_TWO_END_TIME)
                .description(BE_PM_ONE_TWO_DESCRIPTION)
                .userName(BE_PM_ONE_TWO_USERNAME)
                .password(BE_PM_ONE_TWO_PW)
                .space(be)
                .build();

        getReservationIdAfterSave(beReservationApi, beNextDayAmSixTwelveRequest);

        fe1ZeroOne = Reservation.builder()
                .id(getReservationIdAfterSave(fe1ReservationApi, feZeroOneRequest))
                .startTime(FE1_AM_TEN_ELEVEN_START_TIME)
                .endTime(FE1_AM_TEN_ELEVEN_END_TIME)
                .description(FE1_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(FE1_AM_TEN_ELEVEN_USERNAME)
                .password(FE1_AM_TEN_ELEVEN_PW)
                .space(fe)
                .build();
    }

    private Long getReservationIdAfterSave(
            final String api,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
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

    private ExtractableResponse<Response> updateReservation(
            final String api,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/guest/put", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateWithPasswordRequest)
                .when().put(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservation(
            final String api,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
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
}
