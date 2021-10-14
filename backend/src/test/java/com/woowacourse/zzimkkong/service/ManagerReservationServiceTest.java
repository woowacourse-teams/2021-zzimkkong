package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginEmailDto;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.service.strategy.ManagerReservationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
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

class ManagerReservationServiceTest extends ServiceTest {
    private static final String CHANGED_NAME = "이름 변경";
    private static final String CHANGED_DESCRIPTION = "회의명 변경";

    @Autowired
    private ReservationService reservationService;
    private final ManagerReservationStrategy managerReservationStrategy = new ManagerReservationStrategy();

    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
            THE_DAY_AFTER_TOMORROW.atTime(13, 0),
            THE_DAY_AFTER_TOMORROW.atTime(14, 0),
            RESERVATION_PW,
            USER_NAME,
            DESCRIPTION);
    private final ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
            THE_DAY_AFTER_TOMORROW.atTime(13, 0),
            THE_DAY_AFTER_TOMORROW.atTime(14, 0),
            CHANGED_NAME,
            CHANGED_DESCRIPTION);

    private Member pobi;
    private Member sakjung;
    private LoginEmailDto pobiEmail;
    private LoginEmailDto sakjungEmail;
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
        pobi = new Member(EMAIL, PW, ORGANIZATION);
        sakjung = new Member(NEW_EMAIL, PW, ORGANIZATION);
        pobiEmail = LoginEmailDto.from(EMAIL);
        sakjungEmail = LoginEmailDto.from(NEW_EMAIL);
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
        noneExistingMapId = luther.getId() + 1;
        noneExistingSpaceId = (long) (luther.getSpaces().size() + 1);
    }

    @Test
    @DisplayName("예약 생성 요청 시, mapId와 요청이 들어온다면 예약을 생성한다.")
    void save() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);


        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy);

        //then
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @Test
    @DisplayName("예약 생성 요청 시, 과거의 예약도 생성한다.")
    void savePastReservation() {
        //given
        Reservation pastReservation = Reservation.builder()
                .id(1L)
                .startTime(BE_AM_TEN_ELEVEN_START_TIME.minusDays(5))
                .endTime(BE_AM_TEN_ELEVEN_END_TIME.minusDays(5))
                .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(BE_AM_TEN_ELEVEN_USERNAME)
                .password(BE_AM_TEN_ELEVEN_PW)
                .space(be)
                .build();
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.save(any(Reservation.class)))
                .willReturn(pastReservation);

        //when
        ReservationCreateUpdateWithPasswordRequest pastReservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                pastReservation.getStartTime(),
                pastReservation.getEndTime(),
                pastReservation.getPassword(),
                pastReservation.getUserName(),
                pastReservation.getDescription());
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                luther.getId(),
                be.getId(),
                pastReservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy);

        //then
        assertThat(reservationCreateResponse.getId()).isEqualTo(pastReservation.getId());
    }

    @Test
    @DisplayName("예약 생성 요청 시, mapId에 따른 map이 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistMapException() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                noneExistingMapId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, map에 대한 권한이 없다면 예외가 발생한다.")
    void saveNoAuthorityOnMapException() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(sakjung));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                sakjungEmail);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, spaceId에 따른 space가 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistSpaceException() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                noneExistingSpaceId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 종료 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveEndTimeBeforeNow() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(14, 0),
                THE_DAY_AFTER_TOMORROW.atTime(13, 0),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간이 같다면 예외가 발생한다.")
    void saveStartTimeEqualsEndTime() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간의 날짜가 다르다면 예외가 발생한다.")
    void saveStartTimeDateNotEqualsEndTimeDate() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusDays(1),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"9:10", "22:23"}, delimiter = ':')
    @DisplayName("예약 생성 요청 시, 공간의 예약가능 시간이 아니라면 예외가 발생한다.")
    void saveInvalidTimeSetting(int startTime, int endTime) {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startTime, 0),
                THE_DAY_AFTER_TOMORROW.atTime(endTime, 30),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy))
                .isInstanceOf(InvalidStartEndTimeException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"60:0", "0:60"}, delimiter = ':')
    @DisplayName("예약 생성 요청 시, 이미 겹치는 시간이 존재하면 예외가 발생한다.")
    void saveAvailabilityException(int startMinute, int endMinute) {
        //given, when
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndDate(
                anyList(),
                any(LocalDate.class)))
                .willReturn(List.of(makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().minusMinutes(startMinute),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().plusMinutes(endMinute),
                        be)));

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy))
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
                .name("예약이 불가능한 공간")
                .color("#FED7D9")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        Long closedSpaceId = closedSpace.getId();

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                closedSpaceId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        // then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy)).
                isInstanceOf(InvalidReservationEnableException.class);
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

        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        Long invalidDayOfWeekSpaceId = invalidDayOfWeekSpace.getId();

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                invalidDayOfWeekSpaceId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        // then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy)).
                isInstanceOf(InvalidDayOfWeekException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = 60)
    @DisplayName("예약 생성 요청 시, 경계값이 일치한다면 생성된다.")
    void saveSameThresholdTime(int duration) {
        //given, when
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndDate(
                anyList(),
                any(LocalDate.class)))
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
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        //then
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy);
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @ParameterizedTest
    @CsvSource({"1,61", "10,55", "5,65", "20,89"})
    @DisplayName("예약 생성/수정 요청 시, space setting의 reservationTimeUnit이 일치하지 않으면 예외가 발생한다.")
    void saveReservationTimeUnitException(int additionalStartMinute, int additionalEndMinute) {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        LocalDateTime theDayAfterTomorrowTen = THE_DAY_AFTER_TOMORROW.atTime(10, 0);

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                theDayAfterTomorrowTen.plusMinutes(additionalStartMinute),
                theDayAfterTomorrowTen.plusMinutes(additionalEndMinute),
                RESERVATION_PW,
                USER_NAME,
                DESCRIPTION);
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                theDayAfterTomorrowTen.plusMinutes(additionalStartMinute),
                theDayAfterTomorrowTen.plusMinutes(additionalEndMinute),
                USER_NAME,
                DESCRIPTION);

        Long reservationId = reservation.getId();

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy))
                .isInstanceOf(InvalidTimeUnitException.class);
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                managerReservationStrategy))
                .isInstanceOf(InvalidTimeUnitException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {50, 130})
    @DisplayName("예약 생성/수정 요청 시, space setting의 minimum, maximum 시간이 옳지 않으면 예외가 발생한다.")
    void saveReservationMinimumMaximumTimeUnitException(int duration) {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
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
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusMinutes(duration),
                USER_NAME,
                DESCRIPTION);

        Long reservationId = reservation.getId();

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail);

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(
                reservationCreateDto,
                managerReservationStrategy))
                .isInstanceOf(InvalidDurationTimeException.class);

        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                managerReservationStrategy))
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
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndDate(
                anyList(),
                any(LocalDate.class)))
                .willReturn(foundReservations);

        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                lutherId,
                beId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail);

        //then
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.from(foundReservations);
        assertThat(reservationService.findReservations(
                reservationFindDto,
                managerReservationStrategy))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindResponse);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 맵이 없으면 오류가 발생한다.")
    void findReservationsNotExistMap() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                noneExistingMapId,
                beId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.findReservations(reservationFindDto, managerReservationStrategy))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 공간이 없으면 오류가 발생한다.")
    void findReservationsNotExistSpace() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                lutherId,
                noneExistingSpaceId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.findReservations(
                reservationFindDto,
                managerReservationStrategy))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 공간이 없으면 오류가 발생한다.")
    void findReservationsWithInvalidSpace() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(maps.existsById(anyLong()))
                .willReturn(true);
        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                noneExistingSpaceId,
                reservationId,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.findReservation(
                reservationAuthenticationDto,
                managerReservationStrategy))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("전체 예약이나 특정 공간 예약 조회 요청 시, 해당하는 예약이 없으면 빈 정보가 조회된다.")
    void findEmptyReservations() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findAllBySpaceIdInAndDate(
                anyList(),
                any(LocalDate.class)))
                .willReturn(Collections.emptyList());

        //when
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                lutherId,
                beId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail);
        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                lutherId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail);

        //then
        assertThat(reservationService.findReservations(
                reservationFindDto,
                managerReservationStrategy))
                .usingRecursiveComparison()
                .isEqualTo(ReservationFindResponse.from(Collections.emptyList()));
        assertThat(reservationService.findAllReservations(
                reservationFindAllDto,
                managerReservationStrategy))
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
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndDate(
                anyList(),
                any(LocalDate.class)))
                .willReturn(foundReservations);

        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                lutherId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail);

        //then
        ReservationFindAllResponse reservationFindAllResponse = ReservationFindAllResponse.of(findSpaces, foundReservations);
        assertThat(reservationService.findAllReservations(
                reservationFindAllDto,
                managerReservationStrategy))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindAllResponse);
    }

    @Test
    @DisplayName("전체 예약 조회 요청 시, 맵의 소유자가 아니면 오류가 발생한다.")
    void findAllReservationsNotOwner() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(sakjung));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndDate(
                anyList(),
                any(LocalDate.class)))
                .willReturn(List.of(beAmZeroOne, bePmOneTwo));

        //when
        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                lutherId,
                THE_DAY_AFTER_TOMORROW,
                sakjungEmail);

        //then
        assertThatThrownBy(() -> reservationService.findAllReservations(
                reservationFindAllDto,
                managerReservationStrategy))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("특정 날짜의 예약 조회 요청 시, 맵의 소유자가 아니면 오류가 발생한다.")
    void findReservationsNotOwner() {
        //given, when
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(sakjung));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndDate(
                anyList(),
                any(LocalDate.class)))
                .willReturn(List.of(beAmZeroOne, bePmOneTwo));

        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                lutherId,
                beId,
                THE_DAY_AFTER_TOMORROW,
                sakjungEmail);

        //then
        assertThatThrownBy(() -> reservationService.findReservations(
                reservationFindDto,
                managerReservationStrategy))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 수정을 위한 예약 조회 요청 시, 해당 예약을 반환한다.")
    void findReservation() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                pobiEmail);

        //when
        ReservationResponse actualResponse = reservationService.findReservation(
                reservationAuthenticationDto,
                managerReservationStrategy);

        //then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(ReservationResponse.from(reservation));
    }

    @Test
    @DisplayName("예약 수정을 위한 예약 조회 요청 시, 해당 맵에 대한 권한이 없으면 조회를 할 수 없다.")
    void findReservation_NoAuthority() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(sakjung));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                sakjungEmail);

        //then
        assertThatThrownBy(() -> reservationService.findReservation(
                reservationAuthenticationDto,
                managerReservationStrategy))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 해당 예약이 존재하지 않으면 에러가 발생한다.")
    void findInvalidReservationException() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
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
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.findReservation(
                reservationAuthenticationDto,
                managerReservationStrategy))
                .isInstanceOf(NoSuchReservationException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    @DisplayName("예약 수정 요청 시, 올바른 요청이 들어오면 예약이 수정된다. 과거의 예약도 가능하다.")
    void update(int beforeDay) {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        // when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).minusDays(beforeDay),
                THE_DAY_AFTER_TOMORROW.atTime(11, 0).minusDays(beforeDay),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail);

        //then
        assertDoesNotThrow(() -> reservationService.updateReservation(
                reservationUpdateDto,
                managerReservationStrategy));
        assertThat(reservation.getUserName()).isEqualTo(CHANGED_NAME);
        assertThat(reservation.getDescription()).isEqualTo(CHANGED_DESCRIPTION);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 해당 맵에 대한 권한이 없으면 수정할 수 없다.")
    void updateNoAuthorityException() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(sakjung));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(20, 0),
                THE_DAY_AFTER_TOMORROW.atTime(21, 0),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                sakjungEmail);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                managerReservationStrategy))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    @DisplayName("예약 수정 요청 시, 끝 시간 입력이 옳지 않으면 에러가 발생한다.")
    void updateInvalidEndTimeException(int endTime) {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(12, 0),
                THE_DAY_AFTER_TOMORROW.atTime(12, 0).minusHours(endTime),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                managerReservationStrategy))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 시작 시간과 끝 시간이 같은 날짜가 아니면 에러가 발생한다.")
    void updateInvalidDateException() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusDays(1),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                managerReservationStrategy))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"30:30", "-30:-30"}, delimiter = ':')
    @DisplayName("예약 수정 요청 시, 해당 시간에 예약이 존재하면 에러가 발생한다.")
    void updateImpossibleTimeException(int startTime, int endTime) {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        given(reservations.findAllBySpaceIdInAndDate(anyList(), any()))
                .willReturn(Arrays.asList(
                        beAmZeroOne,
                        bePmOneTwo));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                bePmOneTwo.getStartTime().plusMinutes(startTime),
                bePmOneTwo.getEndTime().plusMinutes(endTime),
                reservation.getUserName(),
                reservation.getDescription());
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                managerReservationStrategy))
                .isInstanceOf(ImpossibleReservationTimeException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"9:10", "22:23"}, delimiter = ':')
    @DisplayName("예약 수정 요청 시, 공간의 예약가능 시간이 아니라면 에러가 발생한다.")
    void updateInvalidTimeSetting(int startTime, int endTime) {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startTime, 0),
                THE_DAY_AFTER_TOMORROW.atTime(endTime, 30),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                managerReservationStrategy))
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

        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
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
                reservationCreateUpdateRequest,
                pobiEmail);

        // then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                managerReservationStrategy))
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

        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
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
                reservationCreateUpdateRequest,
                pobiEmail);

        // then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto,
                managerReservationStrategy))
                .isInstanceOf(InvalidDayOfWeekException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    @DisplayName("예약 삭제 요청이 옳다면 삭제한다. 과거의 예약도 가능하다.")
    void deleteReservation(int beforeDay) {
        //given
        Reservation reservationToDelete = makeReservation(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).minusDays(beforeDay),
                THE_DAY_AFTER_TOMORROW.atTime(12, 0).minusDays(beforeDay),
                be);

        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservationToDelete));

        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                pobiEmail);

        //then
        assertDoesNotThrow(() -> reservationService.deleteReservation(
                reservationAuthenticationDto,
                managerReservationStrategy));
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 맵의 관리자가 아니라면 오류가 발생한다.")
    void deleteNoAuthorityException() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(sakjung));
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                sakjungEmail);

        //then
        assertThatThrownBy(() -> reservationService.deleteReservation(
                reservationAuthenticationDto,
                managerReservationStrategy))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 예약이 존재하지 않는다면 오류가 발생한다.")
    void deleteReservationException() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
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
                pobiEmail);

        //then
        assertThatThrownBy(() -> reservationService.deleteReservation(
                reservationAuthenticationDto,
                managerReservationStrategy))
                .isInstanceOf(NoSuchReservationException.class);
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
