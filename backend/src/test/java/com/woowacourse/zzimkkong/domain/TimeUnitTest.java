package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.IllegalTimeUnitValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;
import java.util.stream.Stream;

import static com.woowacourse.zzimkkong.domain.TimeUnit.MINIMUM_TIME_UNIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeUnitTest {
    @ParameterizedTest
    @DisplayName("TimeUnit 생성시 0 이하의 시간 혹은 5의 배수가 아닌 시간이 들어오면 에러를 반환한다")
    @ValueSource(ints = {-5, 0, 1, 4})
    void timeUnitValueNotDivisibleByMinimumTimeUnit(final int minutes) {
        String message = String.format(IllegalTimeUnitValueException.MESSAGE_FORMAT, minutes);

        assertThatThrownBy(() -> TimeUnit.from(minutes))
                .isInstanceOf(IllegalTimeUnitValueException.class)
                .hasMessage(message);
    }

    @Test
    @DisplayName("5분 부터 120분 까지는 TimeUnit 객체가 캐싱된다")
    void timeUnitCache() {
        for (int i = 1; i <= 24; i++) {
            int minutes = i * MINIMUM_TIME_UNIT;

            TimeUnit thisTimeUnit = TimeUnit.from(minutes);
            TimeUnit thatTimeUnit = TimeUnit.from(minutes);
            assertThat(thisTimeUnit).isSameAs(thatTimeUnit);
        }

        TimeUnit thisTimeUnit = TimeUnit.from(125);
        TimeUnit thatTimeUnit = TimeUnit.from(125);
        assertThat(thisTimeUnit).isNotSameAs(thatTimeUnit);
    }

    @ParameterizedTest
    @DisplayName("TimeUnit이 다른 TimeUnit으로 나누어 떨어지면 true 아니면 false")
    @CsvSource(value = {"5,5,true", "5,10,false", "10,5,true", "25,10,false", "25,5,true"})
    void isDivisibleBy(final int minutes1, final int minutes2, final boolean expectedResult) {
        TimeUnit thisTimeUnit = TimeUnit.from(minutes1);
        TimeUnit thatTimeUnit = TimeUnit.from(minutes2);

        boolean actualResult = thisTimeUnit.isDivisibleBy(thatTimeUnit);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @DisplayName("TimeUnit이 다른 TimeUnit보다 시간이 짧으면 true, 아니면 false")
    @CsvSource(value = {"5,5,false", "5,10,true", "10,5,false", "25,10,false", "15,20,true"})
    void isShorterThan(final int minutes1, final int minutes2, final boolean expectedResult) {
        TimeUnit thisTimeUnit = TimeUnit.from(minutes1);
        TimeUnit thatTimeUnit = TimeUnit.from(minutes2);

        boolean actualResult = thisTimeUnit.isShorterThan(thatTimeUnit);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @DisplayName("timeSlot duration 에 맞는 예약 시간 단위를 반환한다. divisible by minimum possible time unit (i.e. 5)")
    @MethodSource("provideTimeSlots1")
    void getAdjustedIntervalTimeUnit(TimeUnit initialTimeUnit, TimeSlot timeSlot, TimeUnit expected) {
        TimeUnit actual = initialTimeUnit.getAdjustedIntervalTimeUnit(timeSlot);
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("timeSlot duration 에 맞는 예약 (최소, 최대) 가능 시간을 반환한다. divisible by interval time unit & shorter than duration length")
    @MethodSource("provideTimeSlots2")
    void getAdjustedTimeUnit(TimeUnit initialTimeUnit, TimeUnit intervalTimeUnit, TimeSlot timeSlot, TimeUnit expected) {
        TimeUnit actual = initialTimeUnit.getAdjustedTimeUnit(timeSlot, intervalTimeUnit);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideTimeSlots1() {
        return Stream.of(
                Arguments.of(
                        TimeUnit.from(60),
                        TimeSlot.of(
                                LocalTime.of(10, 0),
                                LocalTime.of(11, 30)),
                        TimeUnit.from(30)
                ),
                Arguments.of(
                        TimeUnit.from(60),
                        TimeSlot.of(
                                LocalTime.of(10, 0),
                                LocalTime.of(11, 0)),
                        TimeUnit.from(60)
                ),
                Arguments.of(
                        TimeUnit.from(60),
                        TimeSlot.of(
                                LocalTime.of(10, 0),
                                LocalTime.of(10, 20)),
                        TimeUnit.from(10)
                ),
                Arguments.of(
                        TimeUnit.from(60),
                        TimeSlot.of(
                                LocalTime.of(10, 0),
                                LocalTime.of(10, 30)),
                        TimeUnit.from(30)
                )
        );
    }

    private static Stream<Arguments> provideTimeSlots2() {
        return Stream.of(
                Arguments.of(
                        TimeUnit.from(60),
                        TimeUnit.from(10),
                        TimeSlot.of(
                                LocalTime.of(10, 0),
                                LocalTime.of(11, 30)),
                        TimeUnit.from(60)
                ),
                Arguments.of(
                        TimeUnit.from(60),
                        TimeUnit.from(30),
                        TimeSlot.of(
                                LocalTime.of(10, 0),
                                LocalTime.of(10, 30)),
                        TimeUnit.from(30)
                ),
                Arguments.of(
                        TimeUnit.from(10),
                        TimeUnit.from(5),
                        TimeSlot.of(
                                LocalTime.of(10, 0),
                                LocalTime.of(10, 5)),
                        TimeUnit.from(5)
                )
        );
    }
}
