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

class ManagerReservationControllerTest extends AcceptanceTest {
    @MockBean
    private SlackService slackService;

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

        beReservationApi = saveBeSpaceResponse.header("location") + "/reservations";
        fe1ReservationApi = saveFe1SpaceResponse.header("location") + "/reservations";

        ReservationCreateUpdateAsManagerRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateAsManagerRequest(
                THE_DAY_AFTER_TOMORROW.atTime(15, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(16, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                SALLY_NAME,
                SALLY_DESCRIPTION,
                SALLY_PW,
                null);

        pobi = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
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
        savedReservationId = getReservationIdAfterSave(beReservationApi, reservationCreateUpdateWithPasswordRequest);
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
    @DisplayName("올바른 토큰과 비로그인 예약자의 이름 & 비밀번호가 주어질 때, 비로그인 예약자의 예약을 대신 등록한다.")
    void save_nonLoginUser() {
        //given
        ReservationCreateUpdateAsManagerRequest newReservationCreateUpdateAsManagerRequest = new ReservationCreateUpdateAsManagerRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                SALLY_NAME,
                SALLY_DESCRIPTION,
                SALLY_PW,
                null);

        //when
        ExtractableResponse<Response> response = saveNonLoginReservation(beReservationApi, newReservationCreateUpdateAsManagerRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("올바른 토큰과 로그인 예약자의 이메일이 주어질 때, 로그인 예약자의 예약을 대신 등록한다.")
    void save_loginUser() {
        //given
        ReservationCreateUpdateAsManagerRequest newReservationCreateUpdateAsManagerRequest = new ReservationCreateUpdateAsManagerRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                SALLY_NAME,
                SALLY_DESCRIPTION,
                null,
                EMAIL);

        //when
        ExtractableResponse<Response> response = saveLoginReservation(beReservationApi, newReservationCreateUpdateAsManagerRequest);

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
    @DisplayName("올바른 토큰과 함께 예약 수정을 위한 예약 조회 요청 시, 예약에 대한 정보를 반환한다")
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

    @Test
    @DisplayName("비로그인 예약자의 공간 변경 없는 새로운 예약 정보가 주어지면 예약을 업데이트 한다")
    void update_sameSpace_nonLoginUser() {
        //given
        ReservationCreateUpdateAsManagerRequest reservationCreateUpdateRequestSameSpace = new ReservationCreateUpdateAsManagerRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                "sally",
                "회의입니다.",
                null,
                null
        );
        String api = beReservationApi + "/" + savedReservationId;

        //when
        ExtractableResponse<Response> updateResponse = updateReservationNonLoginUser(api, reservationCreateUpdateRequestSameSpace);
        ExtractableResponse<Response> findResponse = findReservation(api);

        ReservationResponse actualResponse = findResponse.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(
                Reservation.builder()
                        .id(savedReservationId)
                        .reservationTime(
                                ReservationTime.ofDefaultServiceZone(
                                        reservationCreateUpdateRequestSameSpace.localStartDateTime(),
                                        reservationCreateUpdateRequestSameSpace.localEndDateTime()))
                        .description(reservationCreateUpdateRequestSameSpace.getDescription())
                        .userName(reservationCreateUpdateRequestSameSpace.getName())
                        .space(be)
                        .build());

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("로그인 예약자의 공간 변경 없는 새로운 예약 정보가 주어지면 예약을 업데이트 한다")
    void update_sameSpace_loginUser() {
        //given
        ReservationCreateUpdateAsManagerRequest reservationCreateUpdateRequestSameSpace = new ReservationCreateUpdateAsManagerRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                null,
                "회의입니다.",
                null,
                EMAIL
        );
        String api = beReservationApi + "/" + bePmTwoThreeByPobi.getId();

        //when
        ExtractableResponse<Response> updateResponse = updateReservationForLoginUser(api, reservationCreateUpdateRequestSameSpace);
        ExtractableResponse<Response> findResponse = findReservation(api);

        ReservationResponse actualResponse = findResponse.as(ReservationResponse.class);
        ReservationResponse expectedResponse = ReservationResponse.from(
                Reservation.builder()
                        .id(bePmTwoThreeByPobi.getId())
                        .reservationTime(
                                ReservationTime.ofDefaultServiceZone(
                                        reservationCreateUpdateRequestSameSpace.localStartDateTime(),
                                        reservationCreateUpdateRequestSameSpace.localEndDateTime()))
                        .description(reservationCreateUpdateRequestSameSpace.getDescription())
                        .member(pobi)
                        .userName(reservationCreateUpdateRequestSameSpace.getName())
                        .space(be)
                        .build());

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("비로그인 예약자의 공간 변경 있는 새로운 예약 정보가 주어지면 공간을 이동한 채로 예약을 업데이트 한다.")
    void update_spaceUpdate_nonLoginUser() {
        //given
        ReservationCreateUpdateAsManagerRequest reservationCreateUpdateWithPasswordRequestDifferentSpace = new ReservationCreateUpdateAsManagerRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                "sally",
                "회의입니다.",
                null,
                null
        );

        String api = fe1ReservationApi + "/" + savedReservationId;

        //when
        ExtractableResponse<Response> updateResponse = updateReservationNonLoginUser(api, reservationCreateUpdateWithPasswordRequestDifferentSpace);
        ExtractableResponse<Response> findResponse = findReservations(
                fe1ReservationApi,
                THE_DAY_AFTER_TOMORROW.toString());

        ReservationFindResponse actualResponse = findResponse.as(ReservationFindResponse.class);

        List<Reservation> expectedFindReservations = Arrays.asList(
                Reservation.builder()
                        .id(savedReservationId)
                        .reservationTime(
                                ReservationTime.ofDefaultServiceZone(
                                        reservationCreateUpdateWithPasswordRequestDifferentSpace.localStartDateTime(),
                                        reservationCreateUpdateWithPasswordRequestDifferentSpace.localEndDateTime()))
                        .description(reservationCreateUpdateWithPasswordRequestDifferentSpace.getDescription())
                        .userName(reservationCreateUpdateWithPasswordRequestDifferentSpace.getName())
                        .password(savedReservation.getPassword())
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
        ReservationCreateUpdateAsManagerRequest reservationCreateUpdateWithPasswordRequestDifferentSpace = new ReservationCreateUpdateAsManagerRequest(
                THE_DAY_AFTER_TOMORROW.atTime(19, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(20, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                null,
                "회의입니다.",
                null,
                EMAIL);

        String api = fe1ReservationApi + "/" + bePmTwoThreeByPobi.getId();

        //when
        ExtractableResponse<Response> updateResponse = updateReservationForLoginUser(api, reservationCreateUpdateWithPasswordRequestDifferentSpace);
        ExtractableResponse<Response> findResponse = findReservations(
                fe1ReservationApi,
                THE_DAY_AFTER_TOMORROW.toString());

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
                        .userName(pobi.getUserName())
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
    @DisplayName("예약을 삭제한다.")
    void delete() {
        //given, when

        String api = beReservationApi + "/" + savedReservationId;

        //then
        ExtractableResponse<Response> response = deleteReservation(api);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void saveExampleReservations() {
        ReservationCreateUpdateAsManagerRequest beAmZeroOneRequest = new ReservationCreateUpdateAsManagerRequest(
                BE_AM_TEN_ELEVEN_START_TIME_KST,
                BE_AM_TEN_ELEVEN_END_TIME_KST,
                BE_AM_TEN_ELEVEN_USERNAME,
                BE_AM_TEN_ELEVEN_DESCRIPTION,
                BE_AM_TEN_ELEVEN_PW,
                null);

        ReservationCreateUpdateAsManagerRequest bePmOneTwoRequest = new ReservationCreateUpdateAsManagerRequest(
                BE_PM_ONE_TWO_START_TIME_KST,
                BE_PM_ONE_TWO_END_TIME_KST,
                BE_PM_ONE_TWO_USERNAME,
                BE_PM_ONE_TWO_DESCRIPTION,
                BE_PM_ONE_TWO_PW,
                null);

        ReservationCreateUpdateAsManagerRequest beNextDayAmSixTwelveRequest = new ReservationCreateUpdateAsManagerRequest(
                BE_NEXT_DAY_PM_FOUR_TO_SIX_START_TIME_KST,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_END_TIME_KST,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_USERNAME,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_DESCRIPTION,
                BE_NEXT_DAY_PM_FOUR_TO_SIX_PW,
                null);

        ReservationCreateUpdateAsManagerRequest feZeroOneRequest = new ReservationCreateUpdateAsManagerRequest(
                FE1_AM_TEN_ELEVEN_START_TIME_KST,
                FE1_AM_TEN_ELEVEN_END_TIME_KST,
                FE1_AM_TEN_ELEVEN_USERNAME,
                FE1_AM_TEN_ELEVEN_DESCRIPTION,
                FE1_AM_TEN_ELEVEN_PW,
                null);

        ReservationCreateUpdateAsManagerRequest bePmTwoThreeRequestPobi = new ReservationCreateUpdateAsManagerRequest(
                BE_PM_TWO_THREE_START_TIME_KST,
                BE_PM_TWO_THREE_END_TIME_KST,
                BE_PM_TWO_THREE_USERNAME,
                BE_PM_TWO_THREE_DESCRIPTION,
                BE_PM_TWO_THREE_PW,
                EMAIL);

        ReservationCreateUpdateAsManagerRequest beFiveDaysAgoPmTwoThreeRequestPobi = new ReservationCreateUpdateAsManagerRequest(
                BE_FIVE_DAYS_AGO_PM_TWO_THREE_START_TIME_KST,
                BE_FIVE_DAYS_AGO_PM_TWO_THREE_END_TIME_KST,
                BE_FIVE_DAYS_AGO_PM_TWO_THREE_USERNAME,
                BE_FIVE_DAYS_AGO_PM_TWO_THREE_DESCRIPTION,
                BE_FIVE_DAYS_AGO_PM_TWO_THREE_PW,
                EMAIL);

        ReservationCreateUpdateAsManagerRequest fe1PmTwoThreeRequestPobi = new ReservationCreateUpdateAsManagerRequest(
                FE1_PM_TWO_THREE_START_TIME_KST,
                FE1_PM_TWO_THREE_END_TIME_KST,
                FE1_PM_TWO_THREE_USERNAME,
                FE1_PM_TWO_THREE_DESCRIPTION,
                FE1_PM_TWO_THREE_PW,
                EMAIL);

        beAmZeroOne = Reservation.builder()
                .id(getReservationIdAfterSave(beReservationApi, beAmZeroOneRequest))
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
                .id(getReservationIdAfterSave(beReservationApi, bePmOneTwoRequest))
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                BE_PM_ONE_TWO_START_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime(),
                                BE_PM_ONE_TWO_END_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime()))
                .description(BE_PM_ONE_TWO_DESCRIPTION)
                .userName(BE_PM_ONE_TWO_USERNAME)
                .password(BE_PM_ONE_TWO_PW)
                .space(be)
                .build();

        getReservationIdAfterSave(beReservationApi, beNextDayAmSixTwelveRequest);

        fe1ZeroOne = Reservation.builder()
                .id(getReservationIdAfterSave(fe1ReservationApi, feZeroOneRequest))
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
                .id(getReservationIdAfterSave(beReservationApi, bePmTwoThreeRequestPobi))
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
                .id(getReservationIdAfterSave(fe1ReservationApi, fe1PmTwoThreeRequestPobi))
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
                .id(getReservationIdAfterSave(beReservationApi, beFiveDaysAgoPmTwoThreeRequestPobi))
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

    public static Long getReservationIdAfterSave(
            final String api,
            final ReservationCreateUpdateAsManagerRequest reservationCreateUpdateAsManagerRequest) {
        return Long.valueOf(
                saveNonLoginReservation(api, reservationCreateUpdateAsManagerRequest)
                        .header("location")
                        .split("/")[8]);
    }

    static ExtractableResponse<Response> saveNonLoginReservation(
            final String api,
            final ReservationCreateUpdateRequest reservationCreateUpdateAsManagerRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/manager/postForNonLoginUser", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateAsManagerRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> saveLoginReservation(
            final String api,
            final ReservationCreateUpdateRequest reservationCreateUpdateWithPasswordRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/manager/postForLoginUser", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateWithPasswordRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservations(final String api, final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/manager/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParams("date", date)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllReservations(final String api, final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/manager/getAll", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParam("date", date)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateReservationNonLoginUser(
            final String api,
            final ReservationCreateUpdateAsManagerRequest reservationCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/manager/putForNonLoginUser", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateRequest)
                .when().put(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateReservationForLoginUser(
            final String api,
            final ReservationCreateUpdateAsManagerRequest reservationCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/manager/putForLoginUser", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationCreateUpdateRequest)
                .when().put(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findReservation(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("*/*")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/manager/getForUpdate", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteReservation(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("reservation/manager/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(api)
                .then().log().all().extract();
    }
}
