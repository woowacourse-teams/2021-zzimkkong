package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.infrastructure.auth.AuthorizationExtractor;
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

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.ManagerSpaceControllerTest.saveSpace;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class GuestReservationControllerTest extends AcceptanceTest {
    @MockBean
    private SlackService slackService;

    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest;
    private Reservation savedReservation;
    private String beReservationApi;
    private String fe1ReservationApi;
    private Long savedReservationId;

    private Member pobi;

    private Space be;
    private Space fe;

    private Reservation beAmZeroOne;
    private Reservation bePmOneTwo;
    private Reservation fe1ZeroOne;
    private Reservation bePmTwoThreeByPobi;
    private Reservation fePmTwoThreeByPobi;
    private Reservation beFiveDaysAgoPmTwoThreeByPobi;

    @BeforeEach
    void setUp() {
        String lutherId = saveMap("/api/managers/maps", mapCreateUpdateRequest).header("location").split("/")[4];
        String spaceApi = "/api/managers/maps/" + lutherId + "/spaces";
        ExtractableResponse<Response> saveBeSpaceResponse = saveSpace(spaceApi, beSpaceCreateUpdateRequest);
        ExtractableResponse<Response> saveFe1SpaceResponse = saveSpace(spaceApi, feSpaceCreateUpdateRequest);

        Long beSpaceId = Long.valueOf(saveBeSpaceResponse.header("location").split("/")[6]);
        Long feSpaceId = Long.valueOf(saveFe1SpaceResponse.header("location").split("/")[6]);

        beReservationApi = saveBeSpaceResponse.header("location")
                .replaceAll("managers", "guests") + "/reservations";
        fe1ReservationApi = saveFe1SpaceResponse.header("location")
                .replaceAll("managers", "guests") + "/reservations";

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(15, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(16, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                SALLY_PW,
                SALLY_NAME,
                SALLY_DESCRIPTION);

        pobi = Member.builder()
                .email(EMAIL)
                .userName(USER_NAME)
                .password(passwordEncoder.encode(PW))
                .organization(ORGANIZATION)
                .build();
        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);

        Setting beSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        be = Space.builder()
                .id(beSpaceId)
                .name(BE_NAME)
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .spaceSettings(new Settings(List.of(beSetting)))
                .build();

        Setting feSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();

        fe = Space.builder()
                .id(feSpaceId)
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .spaceSettings(new Settings(List.of(feSetting)))
                .build();

        saveExampleReservations();
        savedReservationId = getNonLoginReservationIdAfterSave(beReservationApi, reservationCreateUpdateWithPasswordRequest);
        savedReservation = Reservation.builder()
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                reservationCreateUpdateWithPasswordRequest.localStartDateTime(),
                                reservationCreateUpdateWithPasswordRequest.localEndDateTime()))
                .password(reservationCreateUpdateWithPasswordRequest.getPassword())
                .userName(reservationCreateUpdateWithPasswordRequest.getName())
                .description(reservationCreateUpdateWithPasswordRequest.getDescription())
                .space(be)
                .build();
    }

    @Test
    @DisplayName("비로그인 예약자로써 예약을 등록한다.")
    void save_nonLoginUser() {
        //given
        ReservationCreateUpdateWithPasswordRequest newReservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                SALLY_PW,
                SALLY_NAME,
                SALLY_DESCRIPTION);

        // when
        ExtractableResponse<Response> response = saveNonLoginReservation(beReservationApi, newReservationCreateUpdateWithPasswordRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("로그인 예약자로써 예약을 등록한다.")
    void save_loginUser() {
        //given
        ReservationCreateUpdateWithPasswordRequest newReservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                null,
                null,
                SALLY_DESCRIPTION);

        // when
        ExtractableResponse<Response> response = saveLoginReservation(beReservationApi, newReservationCreateUpdateWithPasswordRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("map id, space id, 특정 날짜가 주어질 때 해당 맵, 해당 공간, 해당 날짜에 속하는 예약들만 찾아온다")
    void find() {
        //given, when
        ExtractableResponse<Response> response = findReservations(beReservationApi, THE_DAY_AFTER_TOMORROW.toString());

        ReservationFindResponse actualResponse = response.as(ReservationFindResponse.class);

        List<Reservation> expectedFindReservations = Arrays.asList(
                savedReservation,
                beAmZeroOne,
                bePmOneTwo,
                bePmTwoThreeByPobi);

        ReservationFindResponse expectedResponse = ReservationFindResponse.from(expectedFindReservations);

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

        List<Reservation> expectedFindReservations = Arrays.asList(
                savedReservation,
                beAmZeroOne,
                bePmOneTwo,
                fe1ZeroOne,
                bePmTwoThreeByPobi,
                fePmTwoThreeByPobi);
        ReservationFindAllResponse expectedResponse = ReservationFindAllResponse.of(
                Arrays.asList(be, fe),
                expectedFindReservations);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringActualNullFields()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("비로그인 예약자의 공간 변경 없는 새로운 예약 정보가 주어지면 예약을 업데이트 한다")
    void update_sameSpace_nonLoginUser() {
        //given
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequestSameSpace = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                reservationCreateUpdateWithPasswordRequest.getPassword(),
                "sally",
                "회의입니다."
        );
        String api = beReservationApi + "/" + savedReservationId;

        //when
        ExtractableResponse<Response> updateResponse = updateNonLoginReservation(api, reservationCreateUpdateWithPasswordRequestSameSpace);
        ExtractableResponse<Response> findResponse = findNonLoginReservation(api, new ReservationPasswordAuthenticationRequest(SALLY_PW));

        ReservationResponse actualResponse = findResponse.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(
                Reservation.builder()
                        .id(savedReservationId)
                        .reservationTime(
                                ReservationTime.ofDefaultServiceZone(
                                        reservationCreateUpdateWithPasswordRequestSameSpace.localStartDateTime(),
                                        reservationCreateUpdateWithPasswordRequestSameSpace.localEndDateTime()))
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
    @DisplayName("로그인 예약자의 공간 변경 없는 새로운 예약 정보가 주어지면 예약을 업데이트 한다")
    void update_sameSpace_loginUser() {
        //given
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequestSameSpace = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                null,
                null,
                "회의입니다."
        );
        String api = beReservationApi + "/" + bePmTwoThreeByPobi.getId();

        //when
        ExtractableResponse<Response> updateResponse = updateLoginReservation(api, reservationCreateUpdateWithPasswordRequestSameSpace);
        ExtractableResponse<Response> findResponse = findLoginReservation(api, new ReservationPasswordAuthenticationRequest(null));

        ReservationResponse actualResponse = findResponse.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(
                Reservation.builder()
                        .id(bePmTwoThreeByPobi.getId())
                        .reservationTime(
                                ReservationTime.ofDefaultServiceZone(
                                        reservationCreateUpdateWithPasswordRequestSameSpace.localStartDateTime(),
                                        reservationCreateUpdateWithPasswordRequestSameSpace.localEndDateTime()))
                        .description(reservationCreateUpdateWithPasswordRequestSameSpace.getDescription())
                        .member(pobi)
                        .userName(bePmTwoThreeByPobi.getUserName())
                        .space(be)
                        .build());

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("비로그인 예약자의 공간 변경 있는 새로운 예약 정보가 주어지면 공간을 이동한 채로 예약을 업데이트 한다.")
    void update_spaceUpdate_nonLoginUser() {
        //given
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequestDifferentSpace = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                SALLY_PW,
                "sally",
                "회의입니다."
        );

        String api = fe1ReservationApi + "/" + savedReservationId;

        //when
        ExtractableResponse<Response> updateResponse = updateNonLoginReservation(api, reservationCreateUpdateWithPasswordRequestDifferentSpace);
        ExtractableResponse<Response> findResponse = findReservations(fe1ReservationApi, THE_DAY_AFTER_TOMORROW.toString());

        ReservationFindResponse actualResponse = findResponse.as(ReservationFindResponse.class);

        List<Reservation> expectedFindReservations = Arrays.asList(
                Reservation.builder()
                        .reservationTime(
                                ReservationTime.ofDefaultServiceZone(
                                        reservationCreateUpdateWithPasswordRequestDifferentSpace.localStartDateTime(),
                                        reservationCreateUpdateWithPasswordRequestDifferentSpace.localEndDateTime()))
                        .description(reservationCreateUpdateWithPasswordRequestDifferentSpace.getDescription())
                        .userName(reservationCreateUpdateWithPasswordRequestDifferentSpace.getName())
                        .password(reservationCreateUpdateWithPasswordRequestDifferentSpace.getPassword())
                        .space(fe)
                        .build(),
                fe1ZeroOne,
                fePmTwoThreeByPobi);

        ReservationFindResponse expectedResponse = ReservationFindResponse.from(expectedFindReservations);

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("로그인 예약자의 공간 변경 있는 새로운 예약 정보가 주어지면 공간을 이동한 채로 예약을 업데이트 한다.")
    void update_spaceUpdate_loginUser() {
        //given
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequestDifferentSpace = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                null,
                null,
                "회의입니다."
        );

        String api = fe1ReservationApi + "/" + bePmTwoThreeByPobi.getId();

        //when
        ExtractableResponse<Response> updateResponse = updateLoginReservation(api, reservationCreateUpdateWithPasswordRequestDifferentSpace);
        ExtractableResponse<Response> findResponse = findReservations(fe1ReservationApi, THE_DAY_AFTER_TOMORROW.toString());

        ReservationFindResponse actualResponse = findResponse.as(ReservationFindResponse.class);

        List<Reservation> expectedFindReservations = Arrays.asList(
                Reservation.builder()
                        .id(bePmTwoThreeByPobi.getId())
                        .reservationTime(
                                ReservationTime.ofDefaultServiceZone(
                                        reservationCreateUpdateWithPasswordRequestDifferentSpace.localStartDateTime(),
                                        reservationCreateUpdateWithPasswordRequestDifferentSpace.localEndDateTime()))
                        .description(reservationCreateUpdateWithPasswordRequestDifferentSpace.getDescription())
                        .member(pobi)
                        .userName(bePmTwoThreeByPobi.getUserName())
                        .space(fe)
                        .build(),
                fe1ZeroOne,
                fePmTwoThreeByPobi);

        ReservationFindResponse expectedResponse = ReservationFindResponse.from(expectedFindReservations);

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("비로그인 예약은 비밀번호 검증을 통해 예약을 삭제한다.")
    void delete_nonLoginUser() {
        //given
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest(SALLY_PW);
        String api = beReservationApi + "/" + savedReservationId;

        //when
        ExtractableResponse<Response> response = deleteNonLoginReservation(api, reservationPasswordAuthenticationRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("로그인 예약은 토큰 검증을 통해 예약을 삭제한다.")
    void delete_loginUser() {
        //given
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest(null);
        String api = beReservationApi + "/" + bePmTwoThreeByPobi.getId();

        //when
        ExtractableResponse<Response> response = deleteLoginReservation(api, reservationPasswordAuthenticationRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("올바른 비밀번호와 함께 예약 수정을 위한 예약 조회 요청 시, 예약에 대한 정보를 반환한다")
    void findOne_nonLoginUser() {
        //given, when
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest(SALLY_PW);
        ExtractableResponse<Response> response = findNonLoginReservation(beReservationApi + "/" + savedReservationId, reservationPasswordAuthenticationRequest);

        ReservationResponse actualResponse = response.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(savedReservation);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("올바른 토큰과 함께 예약 수정을 위한 예약 조회 요청 시, 예약에 대한 정보를 반환한다")
    void findOne_loginUser() {
        //given, when
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest(null);
        ExtractableResponse<Response> response = findLoginReservation(beReservationApi + "/" + bePmTwoThreeByPobi.getId(), reservationPasswordAuthenticationRequest);

        ReservationResponse actualResponse = response.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(bePmTwoThreeByPobi);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("로그인 한 예약자가 다가오는 예약 내역을 조회할 떄, 해당 예약자가 예약한 내역들을 반환한다")
    void findAllUpcomingReservations() {
        // given

        // when
        ExtractableResponse<Response> response = findAllUpcomingReservations("/api/guests/reservations?page=0");
        ReservationInfiniteScrollResponse actualResponse = response.as(ReservationInfiniteScrollResponse.class);
        ReservationInfiniteScrollResponse expectedResponse = ReservationInfiniteScrollResponse.of(
                List.of(bePmTwoThreeByPobi, fePmTwoThreeByPobi),
                false,
                0);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("로그인 한 예약자가 이전 예약 내역을 조회할 떄, 해당 예약자의 이전 예약 내역들을 반환한다")
    void findAllPreviousReservations() {
        // given

        // when
        ExtractableResponse<Response> response = findAllPreviousReservations("/api/guests/reservations/history?page=0");
        ReservationInfiniteScrollResponse actualResponse = response.as(ReservationInfiniteScrollResponse.class);
        ReservationInfiniteScrollResponse expectedResponse = ReservationInfiniteScrollResponse.of(
                List.of(beFiveDaysAgoPmTwoThreeByPobi),
                false,
                0);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    private void saveExampleReservations() {
        ReservationCreateUpdateWithPasswordRequest beAmZeroOneRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE_AM_TEN_ELEVEN_START_TIME_KST,
                BE_AM_TEN_ELEVEN_END_TIME_KST,
                BE_AM_TEN_ELEVEN_PW,
                BE_AM_TEN_ELEVEN_USERNAME,
                BE_AM_TEN_ELEVEN_DESCRIPTION);

        ReservationCreateUpdateWithPasswordRequest bePmOneTwoRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE_PM_ONE_TWO_START_TIME_KST,
                BE_PM_ONE_TWO_END_TIME_KST,
                BE_PM_ONE_TWO_PW,
                BE_PM_ONE_TWO_USERNAME,
                BE_PM_ONE_TWO_DESCRIPTION);

        ReservationCreateUpdateWithPasswordRequest beNextDayAmSixTwelveRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE_NEXT_DAY_PM_FOUR_TO_SIX_START_TIME_KST,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_END_TIME_KST,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_PW,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_USERNAME,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_DESCRIPTION);

        ReservationCreateUpdateWithPasswordRequest feZeroOneRequest = new ReservationCreateUpdateWithPasswordRequest(
                FE1_AM_TEN_ELEVEN_START_TIME_KST,
                FE1_AM_TEN_ELEVEN_END_TIME_KST,
                FE1_AM_TEN_ELEVEN_PW,
                FE1_AM_TEN_ELEVEN_USERNAME,
                FE1_AM_TEN_ELEVEN_DESCRIPTION);

        ReservationCreateUpdateWithPasswordRequest bePmTwoThreeRequestPobi = new ReservationCreateUpdateWithPasswordRequest(
                BE_PM_TWO_THREE_START_TIME_KST,
                BE_PM_TWO_THREE_END_TIME_KST,
                BE_PM_TWO_THREE_PW,
                BE_PM_TWO_THREE_USERNAME,
                BE_PM_TWO_THREE_DESCRIPTION);

        ReservationCreateUpdateRequest beFiveDaysAgoPmTwoThreeRequestPobi = new ReservationCreateUpdateAsManagerRequest(
                BE_FIVE_DAYS_AGO_PM_TWO_THREE_START_TIME_KST,
                BE_FIVE_DAYS_AGO_PM_TWO_THREE_END_TIME_KST,
                BE_FIVE_DAYS_AGO_PM_TWO_THREE_USERNAME,
                BE_FIVE_DAYS_AGO_PM_TWO_THREE_DESCRIPTION,
                BE_FIVE_DAYS_AGO_PM_TWO_THREE_PW,
                EMAIL);

        ReservationCreateUpdateWithPasswordRequest fe1PmTwoThreeRequestPobi = new ReservationCreateUpdateWithPasswordRequest(
                FE1_PM_TWO_THREE_START_TIME_KST,
                FE1_PM_TWO_THREE_END_TIME_KST,
                FE1_PM_TWO_THREE_PW,
                FE1_PM_TWO_THREE_USERNAME,
                FE1_PM_TWO_THREE_DESCRIPTION);

        beAmZeroOne = Reservation.builder()
                .id(getNonLoginReservationIdAfterSave(beReservationApi, beAmZeroOneRequest))
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                BE_AM_TEN_ELEVEN_START_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime(),
                                BE_AM_TEN_ELEVEN_END_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime()))
                .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(BE_AM_TEN_ELEVEN_USERNAME)
                .password(BE_AM_TEN_ELEVEN_PW)
                .space(be)
                .build();

        bePmOneTwo = Reservation.builder()
                .id(getNonLoginReservationIdAfterSave(beReservationApi, bePmOneTwoRequest))
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                BE_PM_ONE_TWO_START_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime(),
                                BE_PM_ONE_TWO_END_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime()))
                .description(BE_PM_ONE_TWO_DESCRIPTION)
                .userName(BE_PM_ONE_TWO_USERNAME)
                .password(BE_PM_ONE_TWO_PW)
                .space(be)
                .build();

        getNonLoginReservationIdAfterSave(beReservationApi, beNextDayAmSixTwelveRequest);

        fe1ZeroOne = Reservation.builder()
                .id(getNonLoginReservationIdAfterSave(fe1ReservationApi, feZeroOneRequest))
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                FE1_AM_TEN_ELEVEN_START_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime(),
                                FE1_AM_TEN_ELEVEN_END_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime()))
                .description(FE1_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(FE1_AM_TEN_ELEVEN_USERNAME)
                .password(FE1_AM_TEN_ELEVEN_PW)
                .space(fe)
                .build();

        bePmTwoThreeByPobi = Reservation.builder()
                .id(getLoginReservationIdAfterSave(beReservationApi, bePmTwoThreeRequestPobi))
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                BE_PM_TWO_THREE_START_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime(),
                                BE_PM_TWO_THREE_END_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime()))
                .description(BE_PM_TWO_THREE_DESCRIPTION)
                .member(pobi)
                .userName(pobi.getUserName())
                .space(be)
                .build();

        fePmTwoThreeByPobi = Reservation.builder()
                .id(getLoginReservationIdAfterSave(fe1ReservationApi, fe1PmTwoThreeRequestPobi))
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                FE1_PM_TWO_THREE_START_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime(),
                                FE1_PM_TWO_THREE_END_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime()))
                .description(FE1_PM_TWO_THREE_DESCRIPTION)
                .member(pobi)
                .userName(pobi.getUserName())
                .space(fe)
                .build();

        beFiveDaysAgoPmTwoThreeByPobi = Reservation.builder()
                .id(getLoginReservationIdAfterSave(beReservationApi.replaceAll("guests", "managers"), beFiveDaysAgoPmTwoThreeRequestPobi))
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                BE_FIVE_DAYS_AGO_PM_TWO_THREE_START_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime(),
                                BE_FIVE_DAYS_AGO_PM_TWO_THREE_END_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime()))
                .description(BE_FIVE_DAYS_AGO_PM_TWO_THREE_DESCRIPTION)
                .member(pobi)
                .userName(pobi.getUserName())
                .password(BE_FIVE_DAYS_AGO_PM_TWO_THREE_USERNAME)
                .space(be)
                .build();
    }

    private Long getNonLoginReservationIdAfterSave(
            final String api,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return Long.valueOf(
                saveNonLoginReservation(api, reservationCreateUpdateWithPasswordRequest)
                        .header("location")
                        .split("/")[8]);
    }

    private Long getLoginReservationIdAfterSave(
            final String api,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        return Long.valueOf(
                ManagerReservationControllerTest.saveLoginReservation(api, reservationCreateUpdateRequest)
                        .header("location")
                        .split("/")[8]);
    }

    private ExtractableResponse<Response> saveNonLoginReservation(
            final String api,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/guest/postForNonLoginUser", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateWithPasswordRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> saveLoginReservation(
            final String api,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/guest/postForLoginUser", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateRequest)
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

    private ExtractableResponse<Response> updateNonLoginReservation(
            final String api,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/guest/putForNonLoginUser", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateWithPasswordRequest)
                .when().put(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateLoginReservation(
            final String api,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/guest/putForLoginUser", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateWithPasswordRequest)
                .when().put(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findNonLoginReservation(
            final String api,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/guest/postForUpdateNonLoginReservation", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationPasswordAuthenticationRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findLoginReservation(
            final String api,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/guest/postForUpdateLoginReservation", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationPasswordAuthenticationRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteNonLoginReservation(
            final String api,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/guest/deleteForNonLoginReservation", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationPasswordAuthenticationRequest)
                .when().delete(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteLoginReservation(
            final String api,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/guest/deleteForLoginReservation", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationPasswordAuthenticationRequest)
                .when().delete(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllUpcomingReservations(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/guest/getAllUpcomingMine", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllPreviousReservations(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/guest/getAllPreviousMine", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }
}
