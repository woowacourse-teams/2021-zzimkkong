package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.map.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.setting.MultipleSettingsException;
import com.woowacourse.zzimkkong.exception.setting.NoSettingAvailableException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;
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

    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
            THE_DAY_AFTER_TOMORROW.atTime(11, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
            THE_DAY_AFTER_TOMORROW.atTime(12, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
            RESERVATION_PW,
            RESERVATION_USER_NAME,
            DESCRIPTION);
    private final ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
            THE_DAY_AFTER_TOMORROW.atTime(11, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
            THE_DAY_AFTER_TOMORROW.atTime(12, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
            CHANGED_NAME,
            CHANGED_DESCRIPTION);

    private Member pobi;
    private Member sakjung;
    private LoginUserEmail pobiEmail;
    private LoginUserEmail sakjungEmail;
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
        pobi = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        sakjung = Member.builder()
                .email(NEW_EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        pobiEmail = LoginUserEmail.from(EMAIL);
        sakjungEmail = LoginUserEmail.from(NEW_EMAIL);
        luther = new Map(1L, LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);

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
                .id(1L)
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
                .id(2L)
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .spaceSettings(new Settings(List.of(feSetting)))
                .build();

        beAmZeroOne = Reservation.builder()
                .id(1L)
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                TimeZoneUtils.convertToUTC(BE_AM_TEN_ELEVEN_START_TIME_KST),
                                TimeZoneUtils.convertToUTC(BE_AM_TEN_ELEVEN_END_TIME_KST)))
                .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(BE_AM_TEN_ELEVEN_USERNAME)
                .password(BE_AM_TEN_ELEVEN_PW)
                .space(be)
                .build();

        bePmOneTwo = Reservation.builder()
                .id(2L)
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                TimeZoneUtils.convertToUTC(BE_PM_ONE_TWO_START_TIME_KST),
                                TimeZoneUtils.convertToUTC(BE_PM_ONE_TWO_END_TIME_KST)))
                .description(BE_PM_ONE_TWO_DESCRIPTION)
                .userName(BE_PM_ONE_TWO_USERNAME)
                .password(BE_PM_ONE_TWO_PW)
                .space(be)
                .build();

        reservation = makeReservation(
                reservationCreateUpdateWithPasswordRequest.localStartDateTime(),
                reservationCreateUpdateWithPasswordRequest.localEndDateTime(),
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
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                luther.getId(),
                be.getId(),
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);


        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(reservationCreateDto);

        //then
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @Test
    @DisplayName("예약 생성 요청 시, 과거의 예약도 생성한다.")
    void savePastReservation() {
        //given
        Reservation pastReservation = Reservation.builder()
                .id(1L)
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                TimeZoneUtils.convertToUTC(BE_AM_TEN_ELEVEN_START_TIME_KST.minusDays(5)),
                                TimeZoneUtils.convertToUTC(BE_AM_TEN_ELEVEN_END_TIME_KST.minusDays(5))))
                .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(BE_AM_TEN_ELEVEN_USERNAME)
                .password(BE_AM_TEN_ELEVEN_PW)
                .space(be)
                .build();
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.save(any(Reservation.class)))
                .willReturn(pastReservation);

        //when
        ZonedDateTime pastReservationStartTimeKST = TimeZoneUtils.convertTo(pastReservation.getStartTime(), ServiceZone.KOREA).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone()));
        ZonedDateTime pastReservationEndTimeKST = TimeZoneUtils.convertTo(pastReservation.getEndTime(), ServiceZone.KOREA).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone()));
        ReservationCreateUpdateWithPasswordRequest pastReservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                pastReservationStartTimeKST,
                pastReservationEndTimeKST,
                pastReservation.getPassword(),
                pastReservation.getUserName(),
                pastReservation.getDescription());
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                luther.getId(),
                be.getId(),
                pastReservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(reservationCreateDto);

        //then
        assertThat(reservationCreateResponse.getId()).isEqualTo(pastReservation.getId());
    }

    @Test
    @DisplayName("예약 생성 요청 시, mapId에 따른 map이 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistMapException() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.empty());

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                noneExistingMapId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, map에 대한 권한이 없다면 예외가 발생한다.")
    void saveNoAuthorityOnMapException() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                sakjungEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, spaceId에 따른 space가 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistSpaceException() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                noneExistingSpaceId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 종료 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveEndTimeBeforeNow() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(14, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(13, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_PW,
                RESERVATION_USER_NAME,
                DESCRIPTION);

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(ImpossibleStartEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간이 같다면 예외가 발생한다.")
    void saveStartTimeEqualsEndTime() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_PW,
                RESERVATION_USER_NAME,
                DESCRIPTION);

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(ImpossibleStartEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간의 날짜가 다르다면 예외가 발생한다.")
    void saveStartTimeDateNotEqualsEndTimeDate() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusDays(1).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_PW,
                RESERVATION_USER_NAME,
                DESCRIPTION);

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(NonMatchingStartEndDateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"60:0", "0:60"}, delimiter = ':')
    @DisplayName("예약 생성 요청 시, 이미 겹치는 시간이 존재하면 예외가 발생한다.")
    void saveAvailabilityException(int startMinute, int endMinute) {
        //given, when
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndReservationTimeDate(
                anyList(),
                any(LocalDate.class)))
                .willReturn(List.of(makeReservation(
                        reservationCreateUpdateWithPasswordRequest.localStartDateTime().minusMinutes(startMinute),
                        reservationCreateUpdateWithPasswordRequest.localEndDateTime().plusMinutes(endMinute),
                        be)));

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(ReservationAlreadyExistsException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 예약이 불가능한 공간이면 에러를 반환한다.")
    void saveReservationUnable() {
        // given, when
        Setting setting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        LocalTime.of(0, 0),
                        LocalTime.of(18, 0)))
                .reservationTimeUnit(TimeUnit.from(10))
                .reservationMinimumTimeUnit(TimeUnit.from(10))
                .reservationMaximumTimeUnit(TimeUnit.from(120))
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Space closedSpace = Space.builder()
                .id(3L)
                .name("예약이 불가능한 공간")
                .color("#FED7D9")
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(false)
                .spaceSettings(new Settings(List.of(setting)))
                .build();

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        Long closedSpaceId = closedSpace.getId();

        //when
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                closedSpaceId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        // then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto)).
                isInstanceOf(InvalidReservationEnableException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 예약이 불가능한 요일이면 에러를 반환한다.")
    void saveIllegalDayOfWeek() {
        // given, when
        Setting setting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        LocalTime.of(0, 0),
                        LocalTime.of(18, 0)))
                .reservationTimeUnit(TimeUnit.from(10))
                .reservationMinimumTimeUnit(TimeUnit.from(10))
                .reservationMaximumTimeUnit(TimeUnit.from(120))
                .enabledDayOfWeek(THE_DAY_AFTER_TOMORROW.plusDays(1L).getDayOfWeek().name())
                .build();

        Space invalidDayOfWeekSpace = Space.builder()
                .id(3L)
                .name("불가능한 요일")
                .color("#FED7D9")
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(true)
                .spaceSettings(new Settings(List.of(setting)))
                .build();

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        Long invalidDayOfWeekSpaceId = invalidDayOfWeekSpace.getId();

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                invalidDayOfWeekSpaceId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        // then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto)).
                isInstanceOf(NoSettingAvailableException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = 60)
    @DisplayName("예약 생성 요청 시, 경계값이 일치한다면 생성된다.")
    void saveSameThresholdTime(int duration) {
        //given, when
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndDateBetween(
                anyList(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .willReturn(List.of(
                        makeReservation(
                                reservationCreateUpdateWithPasswordRequest.localStartDateTime().minusMinutes(duration),
                                reservationCreateUpdateWithPasswordRequest.localEndDateTime().minusMinutes(duration),
                                be),
                        makeReservation(
                                reservationCreateUpdateWithPasswordRequest.localStartDateTime().plusMinutes(duration),
                                reservationCreateUpdateWithPasswordRequest.localEndDateTime().plusMinutes(duration),
                                be)));

        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(reservationCreateDto);
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @ParameterizedTest
    @CsvSource({"6,8", "8,10", "22,23"})
    @DisplayName("예약 생성/수정 요청 시, 예약 시간대에 해당하는 예약 조건이 없으면 에러를 반환한다")
    void saveUpdateReservationNoRelevantSetting(int startHour, int endHour) {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startHour, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(endHour, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_PW,
                RESERVATION_USER_NAME,
                DESCRIPTION);
        Long reservationId = reservation.getId();

        // be setting: 10:00 ~ 22:00
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);
        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(NoSettingAvailableException.class);
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(NoSettingAvailableException.class);
    }

    @ParameterizedTest
    @CsvSource({"7,11", "9,11"})
    @DisplayName("예약 생성/수정 요청 시, 예약 시간대에 해당하는 예약 조건이 2개 이상이면 에러를 반환한다")
    void saveUpdateReservationMultipleSettings(int startHour, int endHour) {
        //given
        be.addSetting(Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0)))
                .reservationTimeUnit(TimeUnit.from(10))
                .reservationMinimumTimeUnit(TimeUnit.from(10))
                .reservationMaximumTimeUnit(TimeUnit.from(60))
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build());
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startHour, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(endHour, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_PW,
                RESERVATION_USER_NAME,
                DESCRIPTION);
        Long reservationId = reservation.getId();

        // be setting: 8:00 ~ 10:00 / 10:00 ~ 22:00
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);
        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(MultipleSettingsException.class);
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(MultipleSettingsException.class);
    }

    @ParameterizedTest
    @CsvSource({"9,11", "21,23"})
    @DisplayName("예약 생성/수정 요청 시, 예약 시간대가 예약 조건안에 완전히 포함되지 않고 걸쳐있으면 에러를 반환한다")
    void saveUpdateReservationIsNotWithinSetting(int startHour, int endHour) {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startHour, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(endHour, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_PW,
                RESERVATION_USER_NAME,
                DESCRIPTION);
        Long reservationId = reservation.getId();

        // be setting: 10:00 ~ 22:00
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);
        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(InvalidStartEndTimeException.class);
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(InvalidStartEndTimeException.class);
    }

    @ParameterizedTest
    @CsvSource({"5,60", "10,55", "5,65", "20,85"})
    @DisplayName("예약 생성/수정 요청 시, space setting의 reservationTimeUnit이 일치하지 않으면 예외가 발생한다.")
    void saveReservationTimeUnitException(int additionalStartMinute, int additionalEndMinute) {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        LocalDateTime theDayAfterTomorrowTen = THE_DAY_AFTER_TOMORROW.atTime(10, 0);

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                theDayAfterTomorrowTen.plusMinutes(additionalStartMinute).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                theDayAfterTomorrowTen.plusMinutes(additionalEndMinute).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_PW,
                RESERVATION_USER_NAME,
                DESCRIPTION);
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                theDayAfterTomorrowTen.plusMinutes(additionalStartMinute).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                theDayAfterTomorrowTen.plusMinutes(additionalEndMinute).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_USER_NAME,
                DESCRIPTION);

        Long reservationId = reservation.getId();

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(InvalidTimeUnitException.class);
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(InvalidTimeUnitException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, space setting의 minimum 시간이 옳지 않으면 예외가 발생한다.")
    void saveReservationMinimumTimeUnitException() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusMinutes(50).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_PW,
                RESERVATION_USER_NAME,
                DESCRIPTION);

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(InvalidMinimumDurationTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, space setting의 maximum 시간이 옳지 않으면 예외가 발생한다.")
    void saveReservationMaximumTimeUnitException() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusMinutes(130).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_PW,
                RESERVATION_USER_NAME,
                DESCRIPTION);

        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                lutherId,
                beId,
                reservationCreateUpdateWithPasswordRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(reservationCreateDto))
                .isInstanceOf(InvalidMaximumDurationTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, space setting의 minimum 시간이 옳지 않으면 예외가 발생한다.")
    void updateReservationMinimumDurationTimeException() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusMinutes(50).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_USER_NAME,
                DESCRIPTION);

        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(InvalidMinimumDurationTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, space setting의 maximum 시간이 옳지 않으면 예외가 발생한다.")
    void updateReservationMaximumDurationTimeException() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusMinutes(130).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                RESERVATION_USER_NAME,
                DESCRIPTION);

        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(InvalidMaximumDurationTimeException.class);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 올바르게 입력하면 해당 날짜, 공간에 대한 예약 정보가 조회된다.")
    void findReservations() {
        //given, when
        int duration = 30;
        List<Reservation> foundReservations = Arrays.asList(
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.localStartDateTime().minusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.localEndDateTime().minusMinutes(duration),
                        be),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.localStartDateTime().plusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.localEndDateTime().plusMinutes(duration),
                        be));

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndReservationTimeDate(
                anyList(),
                any(LocalDate.class)))
                .willReturn(foundReservations);
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));

        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                lutherId,
                beId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.from(foundReservations, pobi);
        assertThat(reservationService.findReservations(
                reservationFindDto))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindResponse);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 맵이 없으면 오류가 발생한다.")
    void findReservationsNotExistMap() {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.empty());

        //when
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                noneExistingMapId,
                beId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.findReservations(reservationFindDto))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 공간이 없으면 오류가 발생한다.")
    void findReservationsNotExistSpace() {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                lutherId,
                noneExistingSpaceId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.findReservations(
                reservationFindDto))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 공간이 없으면 오류가 발생한다.")
    void findReservationsWithInvalidSpace() {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(maps.existsById(anyLong()))
                .willReturn(true);
        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                noneExistingSpaceId,
                reservationId,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.findReservation(
                reservationAuthenticationDto))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("전체 예약이나 특정 공간 예약 조회 요청 시, 해당하는 예약이 없으면 빈 정보가 조회된다.")
    void findEmptyReservations() {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findAllBySpaceIdInAndDateBetween(
                anyList(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .willReturn(Collections.emptyList());
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));

        //when
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                lutherId,
                beId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail,
                ReservationType.Constants.MANAGER);
        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                lutherId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThat(reservationService.findReservations(
                reservationFindDto))
                .usingRecursiveComparison()
                .isEqualTo(ReservationFindResponse.from(Collections.emptyList(), pobi));
        assertThat(reservationService.findAllReservations(
                reservationFindAllDto))
                .usingRecursiveComparison()
                .isEqualTo(ReservationFindAllResponse.of(List.of(be, fe), Collections.emptyList(), pobi));
    }

    @Test
    @DisplayName("전체 예약 조회 요청 시, 올바른 mapId, 날짜를 입력하면 해당 날짜에 존재하는 모든 예약 정보가 조회된다.")
    void findAllReservation() {
        //given
        int duration = 30;
        List<Reservation> foundReservations = List.of(
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.localStartDateTime().minusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.localEndDateTime().minusMinutes(duration),
                        be),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.localStartDateTime().plusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.localEndDateTime().plusMinutes(duration),
                        be),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.localStartDateTime().minusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.localEndDateTime().minusMinutes(duration),
                        fe),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.localStartDateTime().plusMinutes(duration),
                        reservationCreateUpdateWithPasswordRequest.localEndDateTime().plusMinutes(duration),
                        fe));
        List<Space> findSpaces = List.of(be, fe);

        //when

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndReservationTimeDate(
                anyList(),
                any(LocalDate.class)))
                .willReturn(foundReservations);
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));

        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                lutherId,
                THE_DAY_AFTER_TOMORROW,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        ReservationFindAllResponse reservationFindAllResponse = ReservationFindAllResponse.of(findSpaces, foundReservations, pobi);
        assertThat(reservationService.findAllReservations(
                reservationFindAllDto))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindAllResponse);
    }

    @Test
    @DisplayName("전체 예약 조회 요청 시, 맵의 소유자가 아니면 오류가 발생한다.")
    void findAllReservationsNotOwner() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(sakjung));
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndDateBetween(
                anyList(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .willReturn(List.of(beAmZeroOne, bePmOneTwo));

        //when
        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                lutherId,
                THE_DAY_AFTER_TOMORROW,
                sakjungEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.findAllReservations(
                reservationFindAllDto))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("특정 날짜의 예약 조회 요청 시, 맵의 소유자가 아니면 오류가 발생한다.")
    void findReservationsNotOwner() {
        //given, when
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(sakjung));
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findAllBySpaceIdInAndDateBetween(
                anyList(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .willReturn(List.of(beAmZeroOne, bePmOneTwo));

        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                lutherId,
                beId,
                THE_DAY_AFTER_TOMORROW,
                sakjungEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.findReservations(
                reservationFindDto))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 수정을 위한 예약 조회 요청 시, 해당 예약을 반환한다.")
    void findReservation() {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                luther.getId(),
                be.getId(),
                reservation.getId(),
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //when
        ReservationResponse actualResponse = reservationService.findReservation(
                reservationAuthenticationDto);

        //then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(ReservationResponse.from(reservation, pobi));
    }

    @Test
    @DisplayName("예약 수정을 위한 예약 조회 요청 시, 해당 맵에 대한 권한이 없으면 조회를 할 수 없다.")
    void findReservation_NoAuthority() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(sakjung));
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                sakjungEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.findReservation(
                reservationAuthenticationDto))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 해당 예약이 존재하지 않으면 에러가 발생한다.")
    void findInvalidReservationException() {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());
        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.findReservation(
                reservationAuthenticationDto))
                .isInstanceOf(NoSuchReservationException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    @DisplayName("예약 수정 요청 시, 올바른 요청이 들어오면 예약이 수정된다. 과거의 예약도 가능하다.")
    void update(int beforeDay) {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        // when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).minusDays(beforeDay).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(11, 0).minusDays(beforeDay).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertDoesNotThrow(() -> reservationService.updateReservation(
                reservationUpdateDto));
        assertThat(reservation.getUserName()).isEqualTo(CHANGED_NAME);
        assertThat(reservation.getDescription()).isEqualTo(CHANGED_DESCRIPTION);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 해당 맵에 대한 권한이 없으면 수정할 수 없다.")
    void updateNoAuthorityException() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(sakjung));
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(20, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(21, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                sakjungEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    @DisplayName("예약 수정 요청 시, 끝 시간 입력이 옳지 않으면 에러가 발생한다.")
    void updateInvalidEndTimeException(int endTime) {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(12, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(12, 0).minusHours(endTime).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(reservationUpdateDto))
                .isInstanceOf(ImpossibleStartEndTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 시작 시간과 끝 시간이 같은 날짜가 아니면 에러가 발생한다.")
    void updateInvalidDateException() {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn((Optional.of(reservation)));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(10, 0).plusDays(1).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(NonMatchingStartEndDateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"30:30", "-30:-30"}, delimiter = ':')
    @DisplayName("예약 수정 요청 시, 해당 시간에 예약이 존재하면 에러가 발생한다.")
    void updateImpossibleTimeException(int startTime, int endTime) {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        given(reservations.findAllBySpaceIdInAndReservationTimeDate(
                anyList(),
                any(LocalDate.class)))
                .willReturn(Arrays.asList(
                        beAmZeroOne,
                        bePmOneTwo));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                TimeZoneUtils.convertTo(bePmOneTwo.getStartTime(), ServiceZone.KOREA).plusMinutes(startTime).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                TimeZoneUtils.convertTo(bePmOneTwo.getEndTime(), ServiceZone.KOREA).plusMinutes(endTime).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                reservation.getUserName(),
                reservation.getDescription());
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(ReservationAlreadyExistsException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"9:10", "21:22"}, delimiter = ':')
    @DisplayName("예약 수정 요청 시, 공간의 예약가능 시간이 아니라면 에러가 발생한다.")
    void updateInvalidTimeSetting(int startTime, int endTime) {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                THE_DAY_AFTER_TOMORROW.atTime(startTime, 0).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                THE_DAY_AFTER_TOMORROW.atTime(endTime, 30).atZone(ZoneId.of(ServiceZone.KOREA.getTimeZone())),
                CHANGED_NAME,
                CHANGED_DESCRIPTION);
        Long reservationId = reservation.getId();

        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                lutherId,
                beId,
                reservationId,
                reservationCreateUpdateRequest,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(InvalidStartEndTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 예약이 불가능한 공간이면 에러를 반환한다.")
    void updateReservationUnable() {
        // given, when
        Setting setting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        LocalTime.of(0, 0),
                        LocalTime.of(18, 0)))
                .reservationTimeUnit(TimeUnit.from(10))
                .reservationMinimumTimeUnit(TimeUnit.from(10))
                .reservationMaximumTimeUnit(TimeUnit.from(120))
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Space closedSpace = Space.builder()
                .id(3L)
                .name("예약이 불가능한 공간")
                .color("#FED7D9")
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(false)
                .spaceSettings(new Settings(List.of(setting)))
                .build();


        given(maps.findByIdFetch(anyLong()))
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
                pobiEmail,
                ReservationType.Constants.MANAGER);

        // then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(InvalidReservationEnableException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 예약이 불가능한 요일이면 에러를 반환한다.")
    void updateIllegalDayOfWeek() {
        // given, when
        Setting setting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        LocalTime.of(0, 0),
                        LocalTime.of(18, 0)))
                .reservationTimeUnit(TimeUnit.from(10))
                .reservationMinimumTimeUnit(TimeUnit.from(10))
                .reservationMaximumTimeUnit(TimeUnit.from(120))
                .enabledDayOfWeek(THE_DAY_AFTER_TOMORROW.plusDays(1L).getDayOfWeek().name())
                .build();

        Space invalidDayOfWeekSpace = Space.builder()
                .id(3L)
                .name("불가능한 요일")
                .color("#FED7D9")
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(true)
                .spaceSettings(new Settings(List.of(setting)))
                .build();


        given(maps.findByIdFetch(anyLong()))
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
                pobiEmail,
                ReservationType.Constants.MANAGER);

        // then
        assertThatThrownBy(() -> reservationService.updateReservation(
                reservationUpdateDto))
                .isInstanceOf(NoSettingAvailableException.class);
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


        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservationToDelete));

        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertDoesNotThrow(() -> reservationService.deleteReservation(
                reservationAuthenticationDto));
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 맵의 관리자가 아니라면 오류가 발생한다.")
    void deleteNoAuthorityException() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(sakjung));
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                sakjungEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.deleteReservation(
                reservationAuthenticationDto))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 예약이 존재하지 않는다면 오류가 발생한다.")
    void deleteReservationException() {
        //given

        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());
        Long reservationId = reservation.getId();

        //when
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                lutherId,
                beId,
                reservationId,
                pobiEmail,
                ReservationType.Constants.MANAGER);

        //then
        assertThatThrownBy(() -> reservationService.deleteReservation(
                reservationAuthenticationDto))
                .isInstanceOf(NoSuchReservationException.class);
    }

    private Reservation makeReservation(final LocalDateTime startTime, final LocalDateTime endTime, final Space space) {
        return Reservation.builder()
                .id(3L)
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                startTime,
                                endTime))
                .password(reservationCreateUpdateWithPasswordRequest.getPassword())
                .userName(reservationCreateUpdateWithPasswordRequest.getName())
                .description(reservationCreateUpdateWithPasswordRequest.getDescription())
                .space(space)
                .build();
    }
}
