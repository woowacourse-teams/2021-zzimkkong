package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.exception.reservation.InvalidDurationTimeException;
import com.woowacourse.zzimkkong.exception.reservation.InvalidTimeUnitException;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

public class ManagerReservationServiceTest extends ServiceTest {
    private static final String CHANGED_NAME = "이름 변경";
    private static final String CHANGED_DESCRIPTION = "회의명 변경";

    @Autowired
    private ManagerReservationService managerReservationService;
    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
            THE_DAY_AFTER_TOMORROW.atTime(13, 0),
            THE_DAY_AFTER_TOMORROW.atTime(14, 0),
            RESERVATION_PASSWORD,
            USER_NAME,
            DESCRIPTION
    );
    private ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
            THE_DAY_AFTER_TOMORROW.atTime(13, 0),
            THE_DAY_AFTER_TOMORROW.atTime(14, 0),
            CHANGED_NAME,
            CHANGED_DESCRIPTION
    );

    private Member pobi;
    private Member sakjung;
    private Map luther;
    private Space be;
    private Space fe;
    private Reservation beAmZeroOne;
    private Reservation bePmOneTwo;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
        sakjung = new Member(NEW_EMAIL, PASSWORD, ORGANIZATION);
        luther = new Map(1L, LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);

        Setting beSetting = new Setting.Builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        be = new Space.Builder()
                .id(1L)
                .name(BE_NAME)
                .map(luther)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(beSetting)
                .build();

        Setting feSetting = new Setting.Builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();

        fe = new Space.Builder()
                .id(2L)
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .description(FE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(feSetting)
                .build();

        luther.addAllSpaces(List.of(be, fe));

        beAmZeroOne = new Reservation.Builder()
                .id(1L)
                .startTime(BE_AM_TEN_ELEVEN_START_TIME)
                .endTime(BE_AM_TEN_ELEVEN_END_TIME)
                .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(BE_AM_TEN_ELEVEN_USERNAME)
                .password(BE_AM_TEN_ELEVEN_PASSWORD)
                .space(be)
                .build();

        bePmOneTwo = new Reservation.Builder()
                .id(2L)
                .startTime(BE_PM_ONE_TWO_START_TIME)
                .endTime(BE_PM_ONE_TWO_END_TIME)
                .description(BE_PM_ONE_TWO_DESCRIPTION)
                .userName(BE_PM_ONE_TWO_USERNAME)
                .password(BE_PM_ONE_TWO_PASSWORD)
                .space(be)
                .build();

        reservation = makeReservation(
                reservationCreateUpdateWithPasswordRequest.getStartDateTime(),
                reservationCreateUpdateWithPasswordRequest.getEndDateTime(),
                be);
    }

    @Test
    @DisplayName("예약 생성 요청 시, mapId와 요청이 들어온다면 예약을 생성한다.")
    void save() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //when
        ReservationCreateResponse reservationCreateResponse = managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                pobi);

        //then
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @Test
    @DisplayName("예약 생성 요청 시, mapId에 따른 map이 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistMapException() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                pobi))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, map에 대한 권한이 없다면 예외가 발생한다.")
    void saveNoAuthorityOnMapException() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, spaceId에 따른 space가 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistSpaceException() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                luther.getId(),
                3L,
                reservationCreateUpdateWithPasswordRequest,
                pobi))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveStartTimeBeforeNow() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().plusHours(3),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                pobi))
                .isInstanceOf(ImpossibleStartTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 종료 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveEndTimeBeforeNow() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(14,0),
                THE_DAY_AFTER_TOMORROW.atTime(13,0),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                pobi))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간이 같다면 예외가 발생한다.")
    void saveStartTimeEqualsEndTime() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10,0),
                THE_DAY_AFTER_TOMORROW.atTime(10,0),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                pobi))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간의 날짜가 다르다면 예외가 발생한다.")
    void saveStartTimeDateNotEqualsEndTimeDate() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10,0),
                THE_DAY_AFTER_TOMORROW.atTime(10,0).plusDays(1),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                pobi))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"9:10", "22:23"}, delimiter = ':')
    @DisplayName("예약 생성 요청 시, 공간의 예약가능 시간이 아니라면 예외가 발생한다.")
    void saveInvalidTimeSetting(int startTime, int endTime) {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startTime, 0),
                THE_DAY_AFTER_TOMORROW.atTime(endTime, 30),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                pobi))
                .isInstanceOf(InvalidStartEndTimeException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"60:0", "0:60"}, delimiter = ':')
    @DisplayName("예약 생성 요청 시, 이미 겹치는 시간이 존재하면 예외가 발생한다.")
    void saveAvailabilityException(int startMinute, int endMinute) {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                any(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(List.of(makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().minusMinutes(startMinute),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().plusMinutes(endMinute),
                        be)));

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                pobi))
                .isInstanceOf(ImpossibleReservationTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 예약이 불가능한 공간이면 에러를 반환한다.")
    void saveReservationUnable() {
        // given, when
        Setting setting = new Setting.Builder()
                .availableStartTime(LocalTime.of(0, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(1440)
                .reservationEnable(false)
                .enabledDayOfWeek(null)
                .build();

        Space closedSpace = new Space.Builder()
                .id(3L)
                .name("예약이 불가능한 공간")
                .textPosition("bottom")
                .color("#FED7D9")
                .coordinate("100, 90")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        luther.addSpace(closedSpace);

        // then
        assertThatThrownBy(() -> managerReservationService.saveReservation(luther.getId(), closedSpace.getId(), reservationCreateUpdateWithPasswordRequest, pobi))
                .isInstanceOf(InvalidReservationEnableException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 예약이 불가능한 요일이면 에러를 반환한다.")
    void saveIllegalDayOfWeek() {
        // given, when
        Setting setting = new Setting.Builder()
                .availableStartTime(LocalTime.of(0, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(1440)
                .reservationEnable(true)
                .enabledDayOfWeek(THE_DAY_AFTER_TOMORROW.plusDays(1L).getDayOfWeek().name())
                .build();

        Space invalidDayOfWeekSpace = new Space.Builder()
                .id(3L)
                .name("불가능한 요일")
                .textPosition("bottom")
                .color("#FED7D9")
                .coordinate("100, 90")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        luther.addSpace(invalidDayOfWeekSpace);

        // then
        assertThatThrownBy(() -> managerReservationService.saveReservation(luther.getId(), invalidDayOfWeekSpace.getId(), reservationCreateUpdateWithPasswordRequest, pobi))
                .isInstanceOf(InvalidDayOfWeekException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = 60)
    @DisplayName("예약 생성 요청 시, 경계값이 일치한다면 생성된다.")
    void saveSameThresholdTime(int duration) {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(List.of(
                        makeReservation(
                                reservationCreateUpdateWithPasswordRequest.getStartDateTime().minusMinutes(duration),
                                reservationCreateUpdateWithPasswordRequest.getEndDateTime().minusMinutes(duration),
                                be),
                        makeReservation(
                                reservationCreateUpdateWithPasswordRequest.getStartDateTime().plusMinutes(duration),
                                reservationCreateUpdateWithPasswordRequest.getEndDateTime().plusMinutes(duration),
                                be)));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //then
        ReservationCreateResponse reservationCreateResponse = managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                pobi);
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 9, 15, 29})
    @DisplayName("예약 생성/수정 요청 시, space setting의 reservationTimeUnit이 일치하지 않으면 예외가 발생한다.")
    void saveReservationTimeUnitException(int minute) {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        LocalDateTime theDayAfterTomorrowTen = THE_DAY_AFTER_TOMORROW.atTime(10, 0);

        //when, then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                new ReservationCreateUpdateWithPasswordRequest(
                        theDayAfterTomorrowTen.plusMinutes(minute),
                        theDayAfterTomorrowTen.plusMinutes(minute).plusMinutes(60),
                        RESERVATION_PASSWORD,
                        USER_NAME,
                        DESCRIPTION
                ),
                pobi)).isInstanceOf(InvalidTimeUnitException.class);
        assertThatThrownBy(() -> managerReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                new ReservationCreateUpdateWithPasswordRequest(
                        theDayAfterTomorrowTen.plusMinutes(minute),
                        theDayAfterTomorrowTen.plusMinutes(minute).plusMinutes(60),
                        RESERVATION_PASSWORD,
                        USER_NAME,
                        DESCRIPTION
                ),
                pobi)).isInstanceOf(InvalidTimeUnitException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {50, 130})
    @DisplayName("예약 생성/수정 요청 시, space setting의 minimum, maximum 시간이 옳지 않으면 예외가 발생한다.")
    void saveReservationMinimumMaximumTimeUnitException(int duration) {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when,then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                new ReservationCreateUpdateWithPasswordRequest(
                        THE_DAY_AFTER_TOMORROW.atTime(10,0),
                        THE_DAY_AFTER_TOMORROW.atTime(10,0).plusMinutes(duration),
                        RESERVATION_PASSWORD,
                        USER_NAME,
                        DESCRIPTION
                ),
                pobi)).isInstanceOf(InvalidDurationTimeException.class);

        assertThatThrownBy(() -> managerReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                new ReservationCreateUpdateWithPasswordRequest(
                        THE_DAY_AFTER_TOMORROW.atTime(10,0),
                        THE_DAY_AFTER_TOMORROW.atTime(10,0).plusMinutes(duration),
                        RESERVATION_PASSWORD,
                        USER_NAME,
                        DESCRIPTION
                ),
                pobi)).isInstanceOf(InvalidDurationTimeException.class);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 올바르게 입력하면 해당 날짜, 공간에 대한 예약 정보가 조회된다.")
    void findReservations() {
        //given, when
        int duration = 30;
        List<Reservation> foundReservations = Arrays.asList(
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().minusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().minusMinutes(duration),
                        be),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().plusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().plusMinutes(duration),
                        be));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(foundReservations);

        //then
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.from(foundReservations);
        assertThat(managerReservationService.findReservations(luther.getId(), be.getId(), THE_DAY_AFTER_TOMORROW, pobi))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindResponse);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 맵이 없으면 오류가 발생한다.")
    void findReservationsNotExistMap() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> managerReservationService.findReservations(luther.getId(), be.getId(), THE_DAY_AFTER_TOMORROW, pobi))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 공간이 없으면 오류가 발생한다.")
    void findReservationsNotExistSpace() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //then
        assertThatThrownBy(() -> managerReservationService.findReservations(
                luther.getId(),
                3L,
                THE_DAY_AFTER_TOMORROW,
                pobi))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("전체 예약이나 특정 공간 예약 조회 요청 시, 해당하는 예약이 없으면 빈 정보가 조회된다.")
    void findEmptyReservations() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(Collections.emptyList());

        //then
        assertThat(managerReservationService.findReservations(luther.getId(), be.getId(), THE_DAY_AFTER_TOMORROW, pobi))
                .usingRecursiveComparison()
                .isEqualTo(ReservationFindResponse.from(Collections.emptyList()));
        assertThat(managerReservationService.findAllReservations(luther.getId(), THE_DAY_AFTER_TOMORROW, pobi))
                .usingRecursiveComparison()
                .isEqualTo(ReservationFindAllResponse.of(List.of(be, fe), Collections.emptyList()));
    }

    @Test
    @DisplayName("전체 예약 조회 요청 시, 올바른 mapId, 날짜를 입력하면 해당 날짜에 존재하는 모든 예약 정보가 조회된다.")
    void findAllReservation() {
        //given
        int duration = 30;
        List<Reservation> foundReservations = List.of(
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().minusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().minusMinutes(duration),
                        be),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().plusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().plusMinutes(duration),
                        be),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().minusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().minusMinutes(duration),
                        fe),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().plusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().plusMinutes(duration),
                        fe));
        List<Space> findSpaces = List.of(be, fe);

        //when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(foundReservations);

        //then
        ReservationFindAllResponse reservationFindAllResponse = ReservationFindAllResponse.of(findSpaces, foundReservations);
        assertThat(managerReservationService.findAllReservations(luther.getId(), THE_DAY_AFTER_TOMORROW, pobi))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindAllResponse);
    }

    @Test
    @DisplayName("전체 예약 조회 요청 시, 맵의 소유자가 아니면 오류가 발생한다.")
    void findAllReservationsNotOwner() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(List.of(beAmZeroOne, bePmOneTwo));

        //then
        assertThatThrownBy(() -> managerReservationService.findAllReservations(luther.getId(), THE_DAY_AFTER_TOMORROW, sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("특정 날짜의 예약 조회 요청 시, 맵의 소유자가 아니면 오류가 발생한다.")
    void findReservationsNotOwner() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(List.of(beAmZeroOne, bePmOneTwo));

        //then
        assertThatThrownBy(() -> managerReservationService.findReservations(luther.getId(), be.getId(), THE_DAY_AFTER_TOMORROW, sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 수정을 위한 예약 조회 요청 시, 해당 예약을 반환한다.")
    void findReservation() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationResponse actualResponse = managerReservationService.findReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                pobi);

        //then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(ReservationResponse.from(reservation));
    }

    @Test
    @DisplayName("예약 수정을 위한 예약 조회 요청 시, 해당 맵에 대한 권한이 없으면 조회를 할 수 없다.")
    void findReservation_NoAuthority() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //then
        assertThatThrownBy(() -> managerReservationService.findReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 해당 예약이 존재하지 않으면 에러가 발생한다.")
    void findInvalidReservationException() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> managerReservationService.findReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                pobi))
                .isInstanceOf(NoSuchReservationException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 올바른 요청이 들어오면 예약이 수정된다.")
    void update() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        // when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                THE_DAY_AFTER_TOMORROW.atTime(11, 0),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertDoesNotThrow(() -> managerReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationCreateUpdateRequest,
                pobi));
        assertThat(reservation.getUserName()).isEqualTo(CHANGED_NAME);
        assertThat(reservation.getDescription()).isEqualTo(CHANGED_DESCRIPTION);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 해당 맵에 대한 권한이 없으면 수정할 수 없다.")
    void updateNoAuthorityException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(20,0),
                THE_DAY_AFTER_TOMORROW.atTime(21,0),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationCreateUpdateRequest,
                sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    @DisplayName("예약 수정 요청 시, 끝 시간 입력이 옳지 않으면 에러가 발생한다.")
    void updateInvalidEndTimeException(int endTime) {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(12,0),
                THE_DAY_AFTER_TOMORROW.atTime(12,0).minusHours(endTime),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationCreateUpdateRequest,
                pobi))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 시작 시간과 끝 시간이 같은 날짜가 아니면 에러가 발생한다.")
    void updateInvalidDateException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10,0),
                THE_DAY_AFTER_TOMORROW.atTime(10,0).plusDays(1),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationCreateUpdateRequest,
                pobi))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"30:30", "-30:-30"}, delimiter = ':')
    @DisplayName("예약 수정 요청 시, 해당 시간에 예약이 존재하면 에러가 발생한다.")
    void updateImpossibleTimeException(int startTime, int endTime) {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(anyList(), any(), any(), any(), any()))
                .willReturn(Arrays.asList(
                        beAmZeroOne,
                        bePmOneTwo
                ));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                bePmOneTwo.getStartTime().plusMinutes(startTime),
                bePmOneTwo.getEndTime().plusMinutes(endTime),
                reservation.getUserName(),
                reservation.getDescription()
        );

        //then
        assertThatThrownBy(() -> managerReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationCreateUpdateRequest,
                pobi))
                .isInstanceOf(ImpossibleReservationTimeException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"9:10", "22:23"}, delimiter = ':')
    @DisplayName("예약 수정 요청 시, 공간의 예약가능 시간이 아니라면 에러가 발생한다.")
    void updateInvalidTimeSetting(int startTime, int endTime) {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startTime, 0),
                THE_DAY_AFTER_TOMORROW.atTime(endTime,30),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationCreateUpdateRequest,
                pobi))
                .isInstanceOf(InvalidStartEndTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 예약이 불가능한 공간이면 에러를 반환한다.")
    void updateReservationUnable() {
        // given, when
        Setting setting = new Setting.Builder()
                .availableStartTime(LocalTime.of(0, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(1440)
                .reservationEnable(false)
                .enabledDayOfWeek(null)
                .build();

        Space closedSpace = new Space.Builder()
                .id(3L)
                .name("예약이 불가능한 공간")
                .textPosition("bottom")
                .color("#FED7D9")
                .coordinate("100, 90")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        luther.addSpace(closedSpace);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        // then
        assertThatThrownBy(() -> managerReservationService.updateReservation(luther.getId(), closedSpace.getId(), reservation.getId(), reservationCreateUpdateRequest, pobi))
                .isInstanceOf(InvalidReservationEnableException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 예약이 불가능한 요일이면 에러를 반환한다.")
    void updateIllegalDayOfWeek() {
        // given, when
        Setting setting = new Setting.Builder()
                .availableStartTime(LocalTime.of(0, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(1440)
                .reservationEnable(true)
                .enabledDayOfWeek(THE_DAY_AFTER_TOMORROW.plusDays(1L).getDayOfWeek().name())
                .build();

        Space invalidDayOfWeekSpace = new Space.Builder()
                .id(3L)
                .name("불가능한 요일")
                .textPosition("bottom")
                .color("#FED7D9")
                .coordinate("100, 90")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        luther.addSpace(invalidDayOfWeekSpace);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        // then
        assertThatThrownBy(() -> managerReservationService.updateReservation(luther.getId(), invalidDayOfWeekSpace.getId(), reservation.getId(), reservationCreateUpdateRequest, pobi))
                .isInstanceOf(InvalidDayOfWeekException.class);
    }

    @Test
    @DisplayName("예약 삭제 요청이 옳다면 삭제한다.")
    void deleteReservation() {
        //given
        Reservation reservationToDelete = makeReservation(
                THE_DAY_AFTER_TOMORROW.atTime(10,0),
                THE_DAY_AFTER_TOMORROW.atTime(12,0),
                be);

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservationToDelete));

        //when, then
        assertDoesNotThrow(() -> managerReservationService.deleteReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                pobi));
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 맵의 관리자가 아니라면 오류가 발생한다.")
    void deleteNoAuthorityException() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //then
        assertThatThrownBy(() -> managerReservationService.deleteReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 예약이 존재하지 않는다면 오류가 발생한다.")
    void deleteReservationException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> managerReservationService.deleteReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                pobi))
                .isInstanceOf(NoSuchReservationException.class);
    }

    private Reservation makeReservation(final LocalDateTime startTime, final LocalDateTime endTime, final Space space) {
        return new Reservation.Builder()
                .id(3L)
                .startTime(startTime)
                .endTime(endTime)
                .password(reservationCreateUpdateWithPasswordRequest.getPassword())
                .userName(reservationCreateUpdateWithPasswordRequest.getName())
                .description(reservationCreateUpdateWithPasswordRequest.getDescription())
                .space(space)
                .build();
    }
}
