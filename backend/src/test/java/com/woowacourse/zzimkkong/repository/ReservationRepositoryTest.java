package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ReservationRepositoryTest extends RepositoryTest {
    private Space be;
    private Space fe;

    private Reservation beAmZeroOne;
    private Reservation bePmOneTwo;
    private Reservation beNextDayAmSixTwelve;
    private Reservation fe1ZeroOne;

    @BeforeEach
    void setUp() {
        Member pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);

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
                .name(BE_NAME)
                .color(BE_COLOR)
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
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .description(FE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(feSetting)
                .build();

        members.save(pobi);
        maps.save(luther);
        spaces.save(be);
        spaces.save(fe);

        beAmZeroOne = new Reservation.Builder()
                .startTime(BE_AM_ZERO_ONE_START_TIME)
                .endTime(BE_AM_ZERO_ONE_END_TIME)
                .description(BE_AM_ZERO_ONE_DESCRIPTION)
                .userName(BE_AM_ZERO_ONE_USERNAME)
                .password(BE_AM_ZERO_ONE_PASSWORD)
                .space(be)
                .build();

        bePmOneTwo = new Reservation.Builder()
                .startTime(BE_PM_ONE_TWO_START_TIME)
                .endTime(BE_PM_ONE_TWO_END_TIME)
                .description(BE_PM_ONE_TWO_DESCRIPTION)
                .userName(BE_PM_ONE_TWO_USERNAME)
                .password(BE_PM_ONE_TWO_PASSWORD)
                .space(be)
                .build();

        beNextDayAmSixTwelve = new Reservation.Builder()
                .startTime(BE_NEXT_DAY_AM_SIX_TWELVE_START_TIME)
                .endTime(BE_NEXT_DAY_AM_SIX_TWELVE_END_TIME)
                .description(BE_NEXT_DAY_AM_SIX_TWELVE_DESCRIPTION)
                .userName(BE_NEXT_DAY_AM_SIX_TWELVE_USERNAME)
                .password(BE_NEXT_DAY_AM_SIX_TWELVE_PASSWORD)
                .space(be)
                .build();

        fe1ZeroOne = new Reservation.Builder()
                .startTime(FE1_ZERO_ONE_START_TIME)
                .endTime(FE1_ZERO_ONE_END_TIME)
                .description(FE1_ZERO_ONE_DESCRIPTION)
                .userName(FE1_ZERO_ONE_USERNAME)
                .password(FE1_ZERO_ONE_PASSWORD)
                .space(fe)
                .build();

        reservations.save(beAmZeroOne);
        reservations.save(bePmOneTwo);
        reservations.save(beNextDayAmSixTwelve);
        reservations.save(fe1ZeroOne);
    }

    @Test
    @DisplayName("예약을 추가할 수 있다.")
    void save() {
        //given
        Reservation be_two_three = new Reservation.Builder()
                .startTime(THE_DAY_AFTER_TOMORROW.atTime(2, 0))
                .endTime(THE_DAY_AFTER_TOMORROW.atTime(3, 0))
                .description("찜꽁 4차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(be)
                .build();

        //when
        final Reservation savedReservation = reservations.save(be_two_three);

        //then
        assertThat(savedReservation.getId()).isNotNull();
        assertThat(savedReservation).isEqualTo(be_two_three);
    }

    @DisplayName("map id, space id, 특정 시간이 주어질 때, 해당 spaceId와 해당 시간에 속하는 예약들만 찾아온다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween() {
        // given, when
        List<Reservation> foundReservations = getReservations(
                List.of(be.getId(), fe.getId()),
                THE_DAY_AFTER_TOMORROW.atTime(0, 0),
                THE_DAY_AFTER_TOMORROW.atTime(14, 0));

        // then
        assertThat(foundReservations).usingRecursiveComparison()
                .isEqualTo(List.of(beAmZeroOne, bePmOneTwo, fe1ZeroOne));
    }

    @DisplayName("특정 시간에 부합하는 예약이 없으면 빈 리스트를 반환한다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween_noMatchingTime() {
        // given, when
        List<Reservation> foundReservations = getReservations(
                List.of(be.getId()),
                THE_DAY_AFTER_TOMORROW.atTime(15, 0),
                THE_DAY_AFTER_TOMORROW.atTime(18, 0));

        // then
        assertThat(foundReservations).isEmpty();
    }

    @DisplayName("특정 공간에 부합하는 예약이 없으면 빈 리스트를 반환한다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween_noMatchingReservation() {
        // given, when
        List<Reservation> foundReservations = getReservations(
                List.of(fe.getId()),
                THE_DAY_AFTER_TOMORROW.atTime(13, 0),
                THE_DAY_AFTER_TOMORROW.atTime(14, 0));

        // then
        assertThat(foundReservations).isEmpty();
    }

    @DisplayName("map id와 특정 날짜가 주어질 때, 해당 날짜에 속하는 해당 map의 모든 space들의 예약들을 찾아온다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween_allSpaces() {
        // given, when
        List<Reservation> foundReservations = getReservations(
                List.of(be.getId(), fe.getId()),
                THE_DAY_AFTER_TOMORROW.atTime(0,0),
                THE_DAY_AFTER_TOMORROW.atTime(0,0).plusDays(1));

        // then
        assertThat(foundReservations).containsExactlyInAnyOrderElementsOf(List.of(beAmZeroOne, bePmOneTwo, fe1ZeroOne));
    }

    @DisplayName("예약을 삭제할 수 있다.")
    @Test
    void delete() {
        //given, when, then
        assertDoesNotThrow(() -> reservations.delete(beNextDayAmSixTwelve));
    }

    @DisplayName("해당 공간에 대한 예약 존재여부를 확인한다.")
    @Test
    void existsBySpace() {
        // given, when, then
        assertThat(reservations.existsBySpaceIdAndEndTimeAfter(fe.getId(), LocalDateTime.now())).isTrue();

        reservations.delete(fe1ZeroOne);
        assertThat(reservations.existsBySpaceIdAndEndTimeAfter(fe.getId(), LocalDateTime.now())).isFalse();
    }

    @ParameterizedTest
    @CsvSource(value = {"0:false", "30:true"}, delimiter = ':')
    @DisplayName("특정 시간 이후의 예약이 존재하는지 확인한다.")
    void existsAllStartTimeAfter(int minusMinute, boolean expected) {
        //given, when
        Boolean actual = reservations.existsBySpaceIdAndEndTimeAfter(be.getId(), BE_NEXT_DAY_AM_SIX_TWELVE_END_TIME.minusMinutes(minusMinute));

        //then
        assertThat(actual).isEqualTo(expected);
    }

    private List<Reservation> getReservations(List<Long> spaceIds, LocalDateTime minimumDateTime, LocalDateTime maximumDateTime) {
        return reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                spaceIds,
                minimumDateTime,
                maximumDateTime,
                minimumDateTime,
                maximumDateTime
        );
    }
}
