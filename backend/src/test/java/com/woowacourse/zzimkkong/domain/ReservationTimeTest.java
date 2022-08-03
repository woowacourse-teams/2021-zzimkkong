package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.ImpossibleStartEndTimeException;
import com.woowacourse.zzimkkong.exception.reservation.NonMatchingStartEndDateException;
import com.woowacourse.zzimkkong.exception.reservation.PastReservationTimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.woowacourse.zzimkkong.Constants.THE_DAY_AFTER_TOMORROW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ReservationTimeTest {
    private final LocalDateTime fourPmUTC = THE_DAY_AFTER_TOMORROW.atTime(16, 0); // next day KST 01:00
    private final LocalDateTime sixPmUTC = THE_DAY_AFTER_TOMORROW.atTime(18, 0); // next day KST 03:00
    private final ReservationTime reservationTime = ReservationTime.of(fourPmUTC, sixPmUTC);

    @Test
    @DisplayName("startTime과 endTime의 KST date가 동일하지 않으면 에러를 반환한다")
    void of_notSameDate() {
        LocalDateTime twoPmUTC = THE_DAY_AFTER_TOMORROW.atTime(14, 0);// KST 23:00
        assertThatThrownBy(() -> ReservationTime.of(twoPmUTC, fourPmUTC))
                .isInstanceOf(NonMatchingStartEndDateException.class);
    }

    @Test
    @DisplayName("endTime이 startTime과 동일하거나 더 이르면 에러를 반환한다")
    void of_invalidStartAndEndTime() {
        assertThatThrownBy(() -> ReservationTime.of(sixPmUTC, fourPmUTC))
                .isInstanceOf(ImpossibleStartEndTimeException.class);
    }

    @Test
    @DisplayName("UTC 시간이 주어질 때, 한국 시간으로 변환(+9시간) 했을 때의 요일을 반환한다")
    void getDayOfWeekKST() {
        DayOfWeek dayOfWeekKST = reservationTime.getDayOfWeekKST();
        DayOfWeek dayOfWeekUTC = fourPmUTC.getDayOfWeek();

        assertThat(dayOfWeekUTC).isNotEqualTo(dayOfWeekKST);
        assertThat(dayOfWeekUTC.plus(1)).isEqualTo(dayOfWeekKST);
    }

    @Test
    @DisplayName("UTC 시간이 주어질 때, 한국 시간으로 변환(+9시간) 했을 때의 TimeSlot을 반환한다")
    void asTimeSlotKST() {
        TimeSlot actualTimeSlot = reservationTime.asTimeSlotKST();
        TimeSlot expectedTimeSlot = TimeSlot.of(
                fourPmUTC.plusHours(9).toLocalTime(),
                sixPmUTC.plusHours(9).toLocalTime());

        assertThat(actualTimeSlot).isEqualTo(expectedTimeSlot);
    }

    @ParameterizedTest
    @DisplayName("주어진 ReservaitonTime이 다른 ReservationTime과 시간대가 겹치면 true, 아니면 false")
    @MethodSource("provideReservationTimes")
    void hasConflictWith(
            final ReservationTime thisReservationTime,
            final ReservationTime thatReservationTime,
            final boolean expectedResult) {
        assertThat(thisReservationTime.hasConflictWith(thatReservationTime)).isEqualTo(expectedResult);
        assertThat(thatReservationTime.hasConflictWith(thisReservationTime)).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("KST 기준으로 date를 가져온다")
    void getDate() {
        LocalDate actualDate = reservationTime.getDate();
        LocalDate expectedDate = fourPmUTC.toLocalDate().plusDays(1);

        assertThat(actualDate).isEqualTo(expectedDate);
    }

    @ParameterizedTest
    @DisplayName("주어진 시간을 포함하면 true 아니면 false")
    @CsvSource(value = {"16,true","15,false","17,true","18,true","19,false"})
    void contains(Integer hour, boolean expectedResult) {
        // 16:00 ~ 18:00
        boolean result = reservationTime.contains(THE_DAY_AFTER_TOMORROW.atTime(hour, 0));
        assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @DisplayName("주어진 시간보다 이전의 예약이면 true 아니면 false")
    @CsvSource(value = {"16,false","15,false","17,false","18,false","19,true"})
    void isBefore(Integer hour, boolean expectedResult) {
        // 16:00 ~ 18:00
        boolean result = reservationTime.isBefore(THE_DAY_AFTER_TOMORROW.atTime(hour, 0));
        assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideReservationTimes() {
        return Stream.of(
                Arguments.of(
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(10, 0), THE_DAY_AFTER_TOMORROW.atTime(10, 5)),
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(10, 0), THE_DAY_AFTER_TOMORROW.atTime(10, 5)),
                        true),
                Arguments.of(
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 10), THE_DAY_AFTER_TOMORROW.atTime(12, 20)),
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 5), THE_DAY_AFTER_TOMORROW.atTime(12, 15)),
                        true),
                Arguments.of(
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 10), THE_DAY_AFTER_TOMORROW.atTime(12, 20)),
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 15), THE_DAY_AFTER_TOMORROW.atTime(12, 20)),
                        true),
                Arguments.of(
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 10), THE_DAY_AFTER_TOMORROW.atTime(12, 20)),
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 15), THE_DAY_AFTER_TOMORROW.atTime(12, 25)),
                        true),
                Arguments.of(
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 10), THE_DAY_AFTER_TOMORROW.atTime(12, 30)),
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 15), THE_DAY_AFTER_TOMORROW.atTime(12, 25)),
                        true),
                Arguments.of(
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 10), THE_DAY_AFTER_TOMORROW.atTime(12, 20)),
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 0), THE_DAY_AFTER_TOMORROW.atTime(12, 5)),
                        false),
                Arguments.of(
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 10), THE_DAY_AFTER_TOMORROW.atTime(12, 20)),
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 25), THE_DAY_AFTER_TOMORROW.atTime(12, 35)),
                        false),
                Arguments.of(
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 10), THE_DAY_AFTER_TOMORROW.atTime(12, 20)),
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 5), THE_DAY_AFTER_TOMORROW.atTime(12, 10)),
                        false),
                Arguments.of(
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 10), THE_DAY_AFTER_TOMORROW.atTime(12, 20)),
                        ReservationTime.of(THE_DAY_AFTER_TOMORROW.atTime(12, 20), THE_DAY_AFTER_TOMORROW.atTime(12, 30)),
                        false)
        );
    }
}