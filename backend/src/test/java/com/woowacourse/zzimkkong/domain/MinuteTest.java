package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.IllegalMinuteValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.woowacourse.zzimkkong.domain.Minute.MINIMUM_TIME_UNIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MinuteTest {
    @ParameterizedTest
    @DisplayName("Minute 생성시 0 이하의 시간 혹은 5의 배수가 아닌 시간이 들어오면 에러를 반환한다")
    @ValueSource(ints = {-5, 0, 1, 4})
    void minuteValueNotDivisibleByMinimumTimeUnit(final int minutes) {
        String message = String.format(IllegalMinuteValueException.MESSAGE_FORMAT, minutes);

        assertThatThrownBy(() -> Minute.from(minutes))
                .isInstanceOf(IllegalMinuteValueException.class)
                .hasMessage(message);
    }

    @Test
    @DisplayName("5분 부터 120분 까지는 Minute 객체가 캐싱된다")
    void MinuteCache() {
        for (int i = 1; i <= 24; i++) {
            int minutes = i * MINIMUM_TIME_UNIT;

            Minute thisMinute = Minute.from(minutes);
            Minute thatMinute = Minute.from(minutes);
            assertThat(thisMinute).isSameAs(thatMinute);
        }

        Minute thisMinute = Minute.from(125);
        Minute thatMinute = Minute.from(125);
        assertThat(thisMinute).isNotSameAs(thatMinute);
    }

    @ParameterizedTest
    @DisplayName("Minute이 다른 Minute으로 나누어 떨어지면 true 아니면 false")
    @CsvSource(value = {"5,5,true", "5,10,false", "10,5,true", "25,10,false", "25,5,true"})
    void isDivisibleBy(final int minute1, final int minute2, final boolean expectedResult) {
        Minute thisMinute = Minute.from(minute1);
        Minute thatMinute = Minute.from(minute2);

        boolean actualResult = thisMinute.isDivisibleBy(thatMinute);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @DisplayName("Minute이 다른 Minute보다 시간이 짧으면 true, 아니면 false")
    @CsvSource(value = {"5,5,false", "5,10,true", "10,5,false", "25,10,false", "15,20,true"})
    void isShorterThan(final int minute1, final int minute2, final boolean expectedResult) {
        Minute thisMinute = Minute.from(minute1);
        Minute thatMinute = Minute.from(minute2);

        boolean actualResult = thisMinute.isShorterThan(thatMinute);
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}