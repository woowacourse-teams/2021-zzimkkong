package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.service.strategy.GuestReservationStrategy;
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
    private ReservationService reservationService;
    private final GuestReservationStrategy guestReservationStrategy = new GuestReservationStrategy();

    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
            THE_DAY_AFTER_TOMORROW.atTime(13, 0),
            THE_DAY_AFTER_TOMORROW.atTime(14, 0),
            RESERVATION_PW,
            USER_NAME,
            DESCRIPTION);

    private final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest(RESERVATION_PW);
    private Map luther;
    private Space be;
    private Space fe;
    private Reservation beAmZeroOne;
    private Reservation bePmOneTwo;
    private Reservation reservation;

    private Long lutherId;
    private Long beId;
    private Long noneExistingMapId;
    private Long noneExistingSpaceId;

    @BeforeEach
    void setUp() {
        Member pobi = new Member(EMAIL, PW, ORGANIZATION);
        luther = new Map(1L, LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);
        
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
                .id(1L)
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
                .id(2L)
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .description(FE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(feSetting)
                .build();

        beAmZeroOne = Reservation.builder()
                .id(1L)
                .startTime(BE_AM_TEN_ELEVEN_START_TIME)
                .endTime(BE_AM_TEN_ELEVEN_END_TIME)
                .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(BE_AM_TEN_ELEVEN_USERNAME)
                .password(BE_AM_TEN_ELEVEN_PW)
                .space(be)
                .build();

        bePmOneTwo = Reservation.builder()
                .id(2L)
                .startTime(BE_PM_ONE_TWO_START_TIME)
                .endTime(BE_PM_ONE_TWO_END_TIME)
                .description(BE_PM_ONE_TWO_DESCRIPTION)
                .userName(BE_PM_ONE_TWO_USERNAME)
                .password(BE_PM_ONE_TWO_PW)
                .space(be)
                .build();

        reservation = makeReservation(
                reservationCreateUpdateWithPasswordRequest.getStartDateTime(),
                reservationCreateUpdateWithPasswordRequest.getEndDateTime(),
                be);

        lutherId = luther.getId();
        beId = be.getId();
        noneExistingMapId = lutherId + 1;
        noneExistingSpaceId = (long) (luther.getSpaces().size() + 1);
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
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest);

        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy);

        //then
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @Test
    @DisplayName("예약 생성 요청 시, mapId에 따른 map이 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistMapException() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                noneExistingMapId,
                beId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, spaceId에 따른 space가 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistSpaceException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        // when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                noneExistingSpaceId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveStartTimeBeforeNow() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        // when
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().plusHours(3),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
                .isInstanceOf(ImpossibleStartTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 종료 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveEndTimeBeforeNow() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        // when
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(14, 0),
                THE_DAY_AFTER_TOMORROW.atTime(13, 0),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간이 같다면 예외가 발생한다.")
    void saveStartTimeEqualsEndTime() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간의 날짜가 다르다면 예외가 발생한다.")
    void saveStartTimeDateNotEqualsEndTimeDate() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusDays(1),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"9:10", "22:23"}, delimiter = ':')
    @DisplayName("예약 생성 요청 시, 공간의 예약가능 시간이 아니라면 예외가 발생한다.")
    void saveInvalidTimeSetting(int startTime, int endTime) {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startTime, 0),
                THE_DAY_AFTER_TOMORROW.atTime(endTime, 30),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
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

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
                .isInstanceOf(ImpossibleReservationTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 예약이 불가능한 공간이면 에러를 반환한다.")
    void saveReservationUnable() {
        // given, when
        Setting setting = Setting.builder()
                .availableStartTime(LocalTime.of(0, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(120)
                .reservationEnable(false)
                .enabledDayOfWeek(null)
                .build();

        Space closedSpace = Space.builder()
                .id(3L)
                .name("백엔드 강의실")
                .color("#FED7D9")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        Long closedSpaceId = closedSpace.getId();

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                closedSpaceId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
                .isInstanceOf(InvalidReservationEnableException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 예약이 불가능한 요일이면 에러를 반환한다.")
    void saveIllegalDayOfWeek() {
        // given, when
        Setting setting = Setting.builder()
                .availableStartTime(LocalTime.of(0, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(120)
                .reservationEnable(true)
                .enabledDayOfWeek(THE_DAY_AFTER_TOMORROW.plusDays(1L).getDayOfWeek().name())
                .build();

        Space invalidDayOfWeekSpace = Space.builder()
                .id(3L)
                .name("불가능한 요일")
                .color("#FED7D9")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        Long invalidDayOfWeekSpaceId = invalidDayOfWeekSpace.getId();

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                invalidDayOfWeekSpaceId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
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

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy);
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

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                theDayAfterTomorrowTen.plusMinutes(minute),
                theDayAfterTomorrowTen.plusMinutes(minute).plusMinutes(60),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest);
        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
                .isInstanceOf(InvalidTimeUnitException.class);
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                guestReservationStrategy))
                .isInstanceOf(InvalidTimeUnitException.class);
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

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusMinutes(duration),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest);
        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                guestReservationStrategy))
                .isInstanceOf(InvalidDurationTimeException.class);

        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                guestReservationStrategy))
                .isInstanceOf(InvalidDurationTimeException.class);
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

        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                lutherId,
                beId,
                THE_DAY_AFTER_TOMORROW);

        //then
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.from(foundReservations);
        assertThat(reservationService.findReservations(
                reservationFindDto,
                guestReservationStrategy))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindResponse);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 맵이 없으면 오류가 발생한다.")
    void findReservationsNotExistMap() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                noneExistingMapId,
                beId,
                THE_DAY_AFTER_TOMORROW);

        //then
        assertThatThrownBy(() -> reservationService.findReservations(
                reservationFindDto,
                guestReservationStrategy))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 공간이 없으면 오류가 발생한다.")
    void findReservationsNotExistSpace() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                lutherId,
                noneExistingSpaceId,
                THE_DAY_AFTER_TOMORROW);

        //then
        assertThatThrownBy(() -> reservationService.findReservations(
                reservationFindDto,
                guestReservationStrategy))
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

        //when
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                lutherId,
                beId,
                THE_DAY_AFTER_TOMORROW);

        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                lutherId,
                THE_DAY_AFTER_TOMORROW);

        //then
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.from(Collections.emptyList());
        assertThat(reservationService.findReservations(
                reservationFindDto,
                guestReservationStrategy))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindResponse);
        assertThat(reservationService.findAllReservations(
                reservationFindAllDto,
                guestReservationStrategy))
                .usingRecursiveComparison()
                .isEqualTo(ReservationFindAllResponse.of(List.of(be, fe), Collections.emptyList()));
    }

    @Test
    @DisplayName("전체 예약 조회 요청 시, 올바른 mapId, 날짜를 입력하면 해당 날짜에 존재하는 모든 예약 정보가 공간의 Id를 기준으로 정렬되어 조회된다.")
    void findAllReservation() {
        //given, when
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

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(foundReservations);

        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                lutherId,
                THE_DAY_AFTER_TOMORROW);

        //then
        ReservationFindAllResponse reservationFindAllResponse = ReservationFindAllResponse.of(findSpaces, foundReservations);
        assertThat(reservationService.findAllReservations(reservationFindAllDto, guestReservationStrategy))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindAllResponse);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 비밀번호가 일치하는지 확인하고 해당 예약을 반환한다.")
    void findReservation() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservation.getId(),
                new ReservationPasswordAuthenticationRequest(reservation.getPassword()));

        ReservationResponse actualResponse = reservationService.findReservation(
                reservationAuthenticationDto,
                guestReservationStrategy);

        //then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(ReservationResponse.from(reservation));
    }

    @Test
    @DisplayName("예약 수정 요청 시, 해당 예약이 존재하지 않으면 에러가 발생한다.")
    void findInvalidReservationException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());
        Long reservationId = reservation.getId();
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest("1111");

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                reservationPasswordAuthenticationRequest);

        //then
        assertThatThrownBy(() -> reservationService.findReservation(
                reservationAuthenticationDto,
                guestReservationStrategy))
                .isInstanceOf(NoSuchReservationException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 비밀번호가 일치하지 않으면 에러가 발생한다.")
    void findWrongPasswordException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        Long reservationId = reservation.getId();
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest = new ReservationPasswordAuthenticationRequest("1111");

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                reservationPasswordAuthenticationRequest);

        //then
        assertThatThrownBy(() -> reservationService.findReservation(
                reservationAuthenticationDto,
                guestReservationStrategy))
                .isInstanceOf(ReservationPasswordException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 올바른 요청이 들어오면 예약이 수정된다.")
    void update() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        Long reservationId = reservation.getId();
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                THE_DAY_AFTER_TOMORROW.atTime(11, 0),
                reservation.getPassword(),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);

        //when
        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertDoesNotThrow(() -> reservationService.updateReservation(
                reservationUpdateDto,
                guestReservationStrategy));
        assertThat(reservation.getUserName()).isEqualTo(CHANGED_NAME);
        assertThat(reservation.getDescription()).isEqualTo(CHANGED_DESCRIPTION);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    @DisplayName("예약 수정 요청 시, 끝 시간 입력이 옳지 않으면 에러가 발생한다.")
    void updateInvalidEndTimeException(int endTime) {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(12, 0),
                THE_DAY_AFTER_TOMORROW.atTime(12, 0).minusHours(endTime),
                reservation.getPassword(),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                guestReservationStrategy))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 시작 시간과 끝 시간이 같은 날짜가 아니면 에러가 발생한다.")
    void updateInvalidDateException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                reservation.getPassword(),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                guestReservationStrategy))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 비밀번호가 일치하지 않으면 에러가 발생한다.")
    void updateIncorrectPasswordException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                reservation.getStartTime(),
                reservation.getEndTime(),
                "1231",
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                guestReservationStrategy))
                .isInstanceOf(ReservationPasswordException.class);
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
                        bePmOneTwo));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                bePmOneTwo.getStartTime().plusMinutes(startTime),
                bePmOneTwo.getEndTime().plusMinutes(endTime),
                reservation.getPassword(),
                reservation.getUserName(),
                reservation.getDescription());
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                guestReservationStrategy))
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
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startTime, 0),
                THE_DAY_AFTER_TOMORROW.atTime(endTime, 30),
                RESERVATION_PW,
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();
        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                guestReservationStrategy))
                .isInstanceOf(InvalidStartEndTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 예약이 불가능한 공간이면 에러를 반환한다.")
    void updateReservationUnable() {
        // given, when
        Setting setting = Setting.builder()
                .availableStartTime(LocalTime.of(0, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(120)
                .reservationEnable(false)
                .enabledDayOfWeek(null)
                .build();

        Space closedSpace = Space.builder()
                .id(3L)
                .name("예약이 불가능한 공간")
                .color("#FED7D9")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        Long closedSpaceId = closedSpace.getId();
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                closedSpaceId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);

        // then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                guestReservationStrategy))
                .isInstanceOf(InvalidReservationEnableException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 예약이 불가능한 요일이면 에러를 반환한다.")
    void updateIllegalDayOfWeek() {
        // given, when
        Setting setting = Setting.builder()
                .availableStartTime(LocalTime.of(0, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(120)
                .reservationEnable(true)
                .enabledDayOfWeek(THE_DAY_AFTER_TOMORROW.plusDays(1L).getDayOfWeek().name())
                .build();

        Space invalidDayOfWeekSpace = Space.builder()
                .id(3L)
                .name("불가능한 요일")
                .color("#FED7D9")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        Long invalidDayOfWeekSpaceId = invalidDayOfWeekSpace.getId();
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                invalidDayOfWeekSpaceId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);

        // then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                guestReservationStrategy))
                .isInstanceOf(InvalidDayOfWeekException.class);
    }

    @Test
    @DisplayName("예약 삭제 요청이 옳다면 삭제한다.")
    void deleteReservation() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime(),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime(),
                        be)));
        Long reservationId = reservation.getId();


        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                reservationPasswordAuthenticationRequest);

        //then
        assertDoesNotThrow(() -> reservationService.deleteReservation(
                reservationAuthenticationDto,
                guestReservationStrategy));
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 예약이 존재하지 않는다면 오류가 발생한다.")
    void deleteReservationException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());
        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                reservationPasswordAuthenticationRequest);

        //then
        assertThatThrownBy(() -> reservationService.deleteReservation(
                reservationAuthenticationDto,
                guestReservationStrategy))
                .isInstanceOf(NoSuchReservationException.class);
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 비밀번호가 일치하지 않는다면 오류가 발생한다.")
    void deleteReservationPasswordException() {
        //given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime(),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime(),
                        be)));

        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest
                = new ReservationPasswordAuthenticationRequest("1233");
        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                reservationPasswordAuthenticationRequest);

        //then
        assertThatThrownBy(() -> reservationService.deleteReservation(
                reservationAuthenticationDto,
                guestReservationStrategy))
                .isInstanceOf(ReservationPasswordException.class);
    }

    private Reservation makeReservation(final LocalDateTime startTime, final LocalDateTime endTime, final Space space) {
        return Reservation.builder()
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
