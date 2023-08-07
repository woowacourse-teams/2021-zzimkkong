package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.IllegalTimeUnitValueException;
import com.woowacourse.zzimkkong.exception.reservation.ImpossibleStartEndTimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeSlotTest {
    @ParameterizedTest
    @DisplayName("endTime이 startTime 보다 작거나 같으면 에러를 반환한다")
    @MethodSource("provideStartAndEndTime_endTimeEqualOrShorterThanStartTime")
    void validateStartEndTime_endTimeEqualOrShorterThanStartTime(final LocalTime startTime, final LocalTime endTime) {
        assertThatThrownBy(() -> TimeSlot.validateStartEndTime(startTime, endTime))
                .isInstanceOf(ImpossibleStartEndTimeException.class);
    }

    @ParameterizedTest
    @DisplayName("startTime이나 endTime의 분 단위가 5의 배수가 아니면 에러를 반환한다")
    @MethodSource("provideStartAndEndTime_notDivisibleByMinimumTimeUnit")
    void validateStartEndTime_startTimeAndEndTimeNotDivisibleByMinimumTimeUnit(final LocalTime startTime, final LocalTime endTime) {
        assertThatThrownBy(() -> TimeSlot.validateStartEndTime(startTime, endTime))
                .isInstanceOf(IllegalTimeUnitValueException.class);
    }

    @ParameterizedTest
    @DisplayName("startTime과 endTime 중 하나라도 TimeUnit으로 나누어 떨어지지 않으면 true, 아니면 false")
    @MethodSource("provideTimeSlotArguments_isNotDivisible")
    void isNotDivisibleBy(
            final TimeSlot timeSlot,
            final TimeUnit timeUnit,
            final boolean expectedResult) {
        assertThat(timeSlot.isNotDivisibleBy(timeUnit)).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("TimeSlot의 duration이 TimeUnit보다 짧으면 true, 아니면 false")
    void isDurationShorterThan() {
        TimeSlot shortTimeSlot = TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 5));
        TimeSlot equalTimeSlot = TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 20));
        TimeSlot longTimeSlot = TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 30));
        TimeUnit timeUnit = TimeUnit.from(10);

        assertThat(shortTimeSlot.isDurationShorterThan(timeUnit)).isTrue();
        assertThat(equalTimeSlot.isDurationShorterThan(timeUnit)).isFalse();
        assertThat(longTimeSlot.isDurationShorterThan(timeUnit)).isFalse();
    }

    @Test
    @DisplayName("TimeSlot의 duration이 TimeUnit보다 길면 true, 아니면 false")
    void isDurationLongerThan() {
        TimeSlot shortTimeSlot = TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 5));
        TimeSlot equalTimeSlot = TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 20));
        TimeSlot longTimeSlot = TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 30));
        TimeUnit timeUnit = TimeUnit.from(10);

        assertThat(shortTimeSlot.isDurationLongerThan(timeUnit)).isFalse();
        assertThat(equalTimeSlot.isDurationLongerThan(timeUnit)).isFalse();
        assertThat(longTimeSlot.isDurationLongerThan(timeUnit)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("TimeSlot이 다른 TimeSlot과 안 겹치면 true, 겹치면 false")
    @MethodSource("provideTimeSlotArguments_isNotWithin")
    void isNotWithin(
            final TimeSlot thisTimeSlot,
            final TimeSlot thatTimeSlot,
            final boolean expectedResult) {
        assertThat(thisTimeSlot.isNotWithin(thatTimeSlot)).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @DisplayName("두 TimeSlot 이 주어질 떄, 겹치는 부분을 반환한다")
    @MethodSource("provideOverlappingTimeSlot")
    void extractOverlappingTimeSlot(
            final TimeSlot thisTimeSlot,
            final TimeSlot thatTimeSlot,
            final TimeSlot expectedResult) {
        assertThat(thisTimeSlot.extractOverlappingTimeSlot(thatTimeSlot)).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @DisplayName("두 TimeSlot 이 주어질 때, 메서드를 호출하는 TimeSlot 이 인자로 들어가는 TimeSlot 에 배타적인 (겹치지 않는) 부분을 반환한다")
    @MethodSource("provideExclusiveTimeSlots")
    void extractExclusiveTimeSlots(final TimeSlot thisTimeSlot,
                                   final TimeSlot thatTimeSlot,
                                   final List<TimeSlot> expectedResult) {
        assertThat(thisTimeSlot.extractExclusiveTimeSlots(thatTimeSlot)).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideStartAndEndTime_endTimeEqualOrShorterThanStartTime() {
        return Stream.of(
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(9, 0)),
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(10, 0))
        );
    }

    private static Stream<Arguments> provideStartAndEndTime_notDivisibleByMinimumTimeUnit() {
        return Stream.of(
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(10, 1)),
                Arguments.of(LocalTime.of(12, 4), LocalTime.of(12, 5)),
                Arguments.of(LocalTime.of(13, 6), LocalTime.of(13, 11))
        );
    }

    private static Stream<Arguments> provideTimeSlotArguments_isNotDivisible() {
        return Stream.of(
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 5)),
                        TimeUnit.from(10),
                        true),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 20)),
                        TimeUnit.from(5),
                        false),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(13, 10), LocalTime.of(13, 30)),
                        TimeUnit.from(20),
                        true)
        );
    }

    private static Stream<Arguments> provideTimeSlotArguments_isNotWithin() {
        return Stream.of(
                Arguments.of(
                        TimeSlot.of(LocalTime.of(12, 5), LocalTime.of(12, 15)),
                        TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 20)),
                        true),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(12, 15), LocalTime.of(12, 25)),
                        TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 20)),
                        true),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(12, 0), LocalTime.of(12, 5)),
                        TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 20)),
                        true),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(12, 25), LocalTime.of(12, 35)),
                        TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 20)),
                        true),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(12, 5), LocalTime.of(12, 10)),
                        TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 20)),
                        true),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(12, 20), LocalTime.of(12, 30)),
                        TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 20)),
                        true),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(12, 15), LocalTime.of(12, 25)),
                        TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 30)),
                        false),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(12, 15), LocalTime.of(12, 20)),
                        TimeSlot.of(LocalTime.of(12, 10), LocalTime.of(12, 20)),
                        false),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 5)),
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 5)),
                        false)
        );
    }

    private static Stream<Arguments> provideOverlappingTimeSlot() {
        return Stream.of(
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 5)),
                        TimeSlot.of(LocalTime.of(10, 5), LocalTime.of(10, 10)),
                        null),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 30)),
                        TimeSlot.of(LocalTime.of(10, 20), LocalTime.of(10, 40)),
                        TimeSlot.of(LocalTime.of(10, 20), LocalTime.of(10, 30))),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 30)),
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 30))),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 30), LocalTime.of(11, 0)),
                        TimeSlot.of(LocalTime.of(10, 30), LocalTime.of(11, 30)),
                        TimeSlot.of(LocalTime.of(10, 30), LocalTime.of(11, 0))),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 30), LocalTime.of(11, 0)),
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(11, 30)),
                        TimeSlot.of(LocalTime.of(10, 30), LocalTime.of(11, 0)))
        );
    }

    private static Stream<Arguments> provideExclusiveTimeSlots() {
        return Stream.of(
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 5)),
                        TimeSlot.of(LocalTime.of(10, 5), LocalTime.of(10, 10)),
                        List.of(TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 5)))),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 30)),
                        TimeSlot.of(LocalTime.of(10, 20), LocalTime.of(10, 40)),
                        List.of(TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 20)))),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 30)),
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                        Collections.emptyList()),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 30), LocalTime.of(11, 0)),
                        TimeSlot.of(LocalTime.of(10, 30), LocalTime.of(11, 30)),
                        Collections.emptyList()),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 30), LocalTime.of(11, 0)),
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(11, 30)),
                        Collections.emptyList()),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(11, 30)),
                        TimeSlot.of(LocalTime.of(10, 30), LocalTime.of(11, 0)),
                        List.of(
                                TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(10, 30)),
                                TimeSlot.of(LocalTime.of(11, 0), LocalTime.of(11, 30))
                        ))
        );
    }
}
