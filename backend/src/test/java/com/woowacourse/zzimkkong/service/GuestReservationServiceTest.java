package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.reservation.*;
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

class GuestReservationServiceTest extends ServiceTest {
    private static final String CHANGED_NAME = "이름 변경";
    private static final String CHANGED_DESCRIPTION = "회의명 변경";

    @Autowired
    private GuestReservationService guestReservationService;

    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
            THE_DAY_AFTER_TOMORROW.atTime(13,0),
            THE_DAY_AFTER_TOMORROW.atTime(14,0),
            RESERVATION_PASSWORD,
            USER_NAME,
            DESCRIPTION
    );

    private final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest
            = new ReservationPasswordAuthenticationRequest(RESERVATION_PASSWORD);
    private Map luther;
    private Space be;
    private Space fe;
    private Reservation beAmZeroOne;
    private Reservation bePmOneTwo;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        Member pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
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

        beAmZeroOne = new Reservation.Builder()
                .id(1L)
                .startTime(BE_AM_ZERO_ONE_START_TIME)
                .endTime(BE_AM_ZERO_ONE_END_TIME)
                .description(BE_AM_ZERO_ONE_DESCRIPTION)
                .userName(BE_AM_ZERO_ONE_USERNAME)
                .password(BE_AM_ZERO_ONE_PASSWORD)
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
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //when
        ReservationCreateResponse reservationCreateResponse = guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @Test
    @DisplayName("예약 생성 요청 시, mapId에 따른 map이 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistMapException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(false);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, spaceId에 따른 space가 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistSpaceException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.empty());
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveStartTimeBeforeNow() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().plusHours(3),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(ImpossibleStartTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 종료 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveEndTimeBeforeNow() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(14, 0),
                THE_DAY_AFTER_TOMORROW.atTime(13, 0),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간이 같다면 예외가 발생한다.")
    void saveStartTimeEqualsEndTime() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간의 날짜가 다르다면 예외가 발생한다.")
    void saveStartTimeDateNotEqualsEndTimeDate() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusDays(1),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"9:10", "22:23"}, delimiter = ':')
    @DisplayName("예약 생성 요청 시, 공간의 예약가능 시간이 아니라면 예외가 발생한다.")
    void saveInvalidTimeSetting(int startTime, int endTime) {
        //given
        saveMock();

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startTime, 0),
                THE_DAY_AFTER_TOMORROW.atTime(endTime, 30),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(InvalidStartEndTimeException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"60:0", "0:60"}, delimiter = ':')
    @DisplayName("예약 생성 요청 시, 이미 겹치는 시간이 존재하면 예외가 발생한다.")
    void saveAvailabilityException(int startMinute, int endMinute) {
        //given, when
        saveMock();
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
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest))
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

        Space be = new Space.Builder()
                .name("백엔드 강의실")
                .textPosition("bottom")
                .color("#FED7D9")
                .coordinate("100, 90")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));

        // then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                this.be.getId(),
                reservationCreateUpdateWithPasswordRequest))
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

        Space be = new Space.Builder()
                .name("백엔드 강의실")
                .textPosition("bottom")
                .color("#FED7D9")
                .coordinate("100, 90")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));

        // then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                this.be.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(InvalidDayOfWeekException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = 60)
    @DisplayName("예약 생성 요청 시, 경계값이 일치한다면 생성된다.")
    void saveSameThresholdTime(int duration) {
        //given, when
        saveMock();
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

        //then
        ReservationCreateResponse reservationCreateResponse = guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest);
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 9, 15, 29})
    @DisplayName("예약 생성/수정 요청 시, space setting의 reservationTimeUnit이 일치하지 않으면 예외가 발생한다.")
    void saveReservationTimeUnitException(int minute) {
        //given
        saveMock();
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        LocalDateTime theDayAfterTomorrowTen = THE_DAY_AFTER_TOMORROW.atTime(10, 0);

        //when, then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                new ReservationCreateUpdateWithPasswordRequest(
                        theDayAfterTomorrowTen.plusMinutes(minute),
                        theDayAfterTomorrowTen.plusMinutes(minute).plusMinutes(60),
                        RESERVATION_PASSWORD,
                        USER_NAME,
                        DESCRIPTION
                ))).isInstanceOf(InvalidTimeUnitException.class);
        assertThatThrownBy(() -> guestReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                new ReservationCreateUpdateWithPasswordRequest(
                        theDayAfterTomorrowTen.plusMinutes(minute),
                        theDayAfterTomorrowTen.plusMinutes(minute).plusMinutes(60),
                        RESERVATION_PASSWORD,
                        USER_NAME,
                        DESCRIPTION
                ))).isInstanceOf(InvalidTimeUnitException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {50, 130})
    @DisplayName("예약 생성/수정 요청 시, space setting의 minimum, maximum 시간이 옳지 않으면 예외가 발생한다.")
    void saveReservationMinimumMaximumTimeUnitException(int duration) {
        //given
        saveMock();
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when,then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                luther.getId(),
                be.getId(),
                new ReservationCreateUpdateWithPasswordRequest(
                        THE_DAY_AFTER_TOMORROW.atTime(10,0),
                        THE_DAY_AFTER_TOMORROW.atTime(10,0).plusMinutes(duration),
                        RESERVATION_PASSWORD,
                        USER_NAME,
                        DESCRIPTION
                ))).isInstanceOf(InvalidDurationTimeException.class);

        assertThatThrownBy(() -> guestReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                new ReservationCreateUpdateWithPasswordRequest(
                        THE_DAY_AFTER_TOMORROW.atTime(10,0),
                        THE_DAY_AFTER_TOMORROW.atTime(10,0).plusMinutes(duration),
                        RESERVATION_PASSWORD,
                        USER_NAME,
                        DESCRIPTION
                ))).isInstanceOf(InvalidDurationTimeException.class);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 올바르게 입력하면 해당 날짜, 공간에 대한 예약 정보가 조회된다.")
    void findReservations() {
        //given
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

        //when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(foundReservations);

        //then
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.from(foundReservations);
        assertThat(guestReservationService.findReservations(
                luther.getId(),
                be.getId(),
                THE_DAY_AFTER_TOMORROW))
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
        assertThatThrownBy(() -> guestReservationService.findReservations(luther.getId(), be.getId(), THE_DAY_AFTER_TOMORROW))
                .isInstanceOf(NoSuchMapException.class);
    }
    
    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 공간이 없으면 오류가 발생한다.")
    void findReservationsNotExistSpace() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findAllByMapId(anyLong()))
                .willReturn(List.of(be, fe));
        //then
        assertThatThrownBy(() -> guestReservationService.findReservations(
                luther.getId(),
                be.getId(),
                THE_DAY_AFTER_TOMORROW))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("전체 예약이나 특정 공간 예약 조회 요청 시, 해당하는 예약이 없으면 빈 정보가 조회된다.")
    void findEmptyReservations() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findAllByMapId(anyLong()))
                .willReturn(List.of(be, fe));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(Collections.emptyList());

        //then
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.from(Collections.emptyList());
        assertThat(guestReservationService.findReservations(
                luther.getId(),
                be.getId(),
                THE_DAY_AFTER_TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindResponse);
        assertThat(guestReservationService.findAllReservations(luther.getId(), THE_DAY_AFTER_TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(ReservationFindAllResponse.of(List.of(be, fe), Collections.emptyList()));
    }

    @Test
    @DisplayName("전체 예약 조회 요청 시, 올바른 mapId, 날짜를 입력하면 해당 날짜에 존재하는 모든 예약 정보가 공간의 Id를 기준으로 정렬되어 조회된다.")
    void findAllReservation() {
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
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findAllByMapId(anyLong()))
                .willReturn(findSpaces);
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(foundReservations);

        //then
        ReservationFindAllResponse reservationFindAllResponse = ReservationFindAllResponse.of(findSpaces, foundReservations);
        assertThat(guestReservationService.findAllReservations(luther.getId(), THE_DAY_AFTER_TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindAllResponse);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 비밀번호가 일치하는지 확인하고 해당 예약을 반환한다.")
    void findReservation() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationResponse actualResponse = guestReservationService.findReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                new ReservationPasswordAuthenticationRequest(reservation.getPassword()));

        //then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(ReservationResponse.from(reservation));
    }

    @Test
    @DisplayName("예약 수정 요청 시, 해당 예약이 존재하지 않으면 에러가 발생한다.")
    void findInvalidReservationException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> guestReservationService.findReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                new ReservationPasswordAuthenticationRequest("1111")))
                .isInstanceOf(NoSuchReservationException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 비밀번호가 일치하지 않으면 에러가 발생한다.")
    void findWrongPasswordException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //then
        assertThatThrownBy(() -> guestReservationService.findReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                new ReservationPasswordAuthenticationRequest("1111")))
                .isInstanceOf(ReservationPasswordException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 올바른 요청이 들어오면 예약이 수정된다.")
    void update() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when, then
        assertDoesNotThrow(() -> guestReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                new ReservationCreateUpdateWithPasswordRequest(
                        THE_DAY_AFTER_TOMORROW.atTime(10,0),
                        THE_DAY_AFTER_TOMORROW.atTime(11,0),
                        reservation.getPassword(),
                        CHANGED_NAME,
                        CHANGED_DESCRIPTION)));
        assertThat(reservation.getUserName()).isEqualTo(CHANGED_NAME);
        assertThat(reservation.getDescription()).isEqualTo(CHANGED_DESCRIPTION);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    @DisplayName("예약 수정 요청 시, 끝 시간 입력이 옳지 않으면 에러가 발생한다.")
    void updateInvalidEndTimeException(int endTime) {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(12,0),
                THE_DAY_AFTER_TOMORROW.atTime(12,0).minusHours(endTime),
                reservation.getPassword(),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> guestReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 시작 시간과 끝 시간이 같은 날짜가 아니면 에러가 발생한다.")
    void updateInvalidDateException() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                reservation.getPassword(),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> guestReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 비밀번호가 일치하지 않으면 에러가 발생한다.")
    void updateIncorrectPasswordException() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                reservation.getStartTime(),
                reservation.getEndTime(),
                "1231",
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> guestReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(ReservationPasswordException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"30:30", "-30:-30"}, delimiter = ':')
    @DisplayName("예약 수정 요청 시, 해당 시간에 예약이 존재하면 에러가 발생한다.")
    void updateImpossibleTimeException(int startTime, int endTime) {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(anyList(), any(), any(), any(), any()))
                .willReturn(Arrays.asList(
                        beAmZeroOne,
                        bePmOneTwo
                ));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                bePmOneTwo.getStartTime().plusMinutes(startTime),
                bePmOneTwo.getEndTime().plusMinutes(endTime),
                reservation.getPassword(),
                reservation.getUserName(),
                reservation.getDescription()
        );

        //then
        assertThatThrownBy(() -> guestReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(ImpossibleReservationTimeException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"9:10", "22:23"}, delimiter = ':')
    @DisplayName("예약 수정 요청 시, 공간의 예약가능 시간이 아니라면 에러가 발생한다.")
    void updateInvalidTimeSetting(int startTime, int endTime) {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startTime, 0),
                THE_DAY_AFTER_TOMORROW.atTime(endTime, 30),
                RESERVATION_PASSWORD,
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> guestReservationService.updateReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationCreateUpdateWithPasswordRequest))
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

        Space be = new Space.Builder()
                .name("백엔드 강의실")
                .textPosition("bottom")
                .color("#FED7D9")
                .coordinate("100, 90")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        // then
        assertThatThrownBy(() -> guestReservationService.updateReservation(luther.getId(), this.be.getId(), reservation.getId(), reservationCreateUpdateWithPasswordRequest))
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

        Space be = new Space.Builder()
                .name("백엔드 강의실")
                .textPosition("bottom")
                .color("#FED7D9")
                .coordinate("100, 90")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        // then
        assertThatThrownBy(() -> guestReservationService.updateReservation(luther.getId(), this.be.getId(), reservation.getId(), reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(InvalidDayOfWeekException.class);
    }

    @Test
    @DisplayName("예약 삭제 요청이 옳다면 삭제한다.")
    void deleteReservation() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime(),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime(),
                        be)));

        //then
        assertDoesNotThrow(() -> guestReservationService.deleteReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationPasswordAuthenticationRequest));
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 예약이 존재하지 않는다면 오류가 발생한다.")
    void deleteReservationException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> guestReservationService.deleteReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationPasswordAuthenticationRequest))
                .isInstanceOf(NoSuchReservationException.class);
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 비밀번호가 일치하지 않는다면 오류가 발생한다.")
    void deleteReservationPasswordException() {
        //given
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest
                = new ReservationPasswordAuthenticationRequest("1233");

        //when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime(),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime(),
                        be)));

        //then
        assertThatThrownBy(() -> guestReservationService.deleteReservation(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                reservationPasswordAuthenticationRequest))
                .isInstanceOf(ReservationPasswordException.class);
    }

    private Reservation makeReservation(final LocalDateTime startTime, final LocalDateTime endTime, final Space space) {
        return new Reservation.Builder()
                .id(1L)
                .startTime(startTime)
                .endTime(endTime)
                .password(reservationCreateUpdateWithPasswordRequest.getPassword())
                .userName(reservationCreateUpdateWithPasswordRequest.getName())
                .description(reservationCreateUpdateWithPasswordRequest.getDescription())
                .space(space)
                .build();
    }

    private void saveMock() {
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);
    }
}
