package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.IllegalMinuteValueException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;

import static com.woowacourse.zzimkkong.exception.reservation.IllegalMinuteValueException.MESSAGE_FORMAT;

@Getter
@NoArgsConstructor
@Embeddable
public class Minute {
    public static final Integer MINIMUM_TIME_UNIT = 5;
    private static final Integer MINIMUM_TIME = 0;
    private static final Map<Integer, Minute> cache = new HashMap<>();

    static {
        for (int i = 1; i <= 24; i++) {
            Integer minutes = i * MINIMUM_TIME_UNIT;
            cache.put(minutes, new Minute(minutes));
        }
    }

    @Column(nullable = false)
    private Integer minute;

    private Minute(final Integer minute) {
        this.minute = minute;

        if (this.minute <= MINIMUM_TIME || this.minute % MINIMUM_TIME_UNIT != 0) {
            throw new IllegalMinuteValueException(this.minute);
        }
    }

    public static Minute from(final int minute) {
        return cache.getOrDefault(minute, new Minute(minute));
    }

    public static Minute from(final long minute) {
        return cache.getOrDefault((int) minute, new Minute((int) minute));
    }

    public boolean isDivisibleBy(final Minute that) {
        return this.minute % that.minute == 0;
    }

    public boolean isShorterThan(final Minute that) {
        return this.minute < that.minute;
    }
}
