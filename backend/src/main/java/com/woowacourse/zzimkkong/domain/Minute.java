package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.MinusMinuteValueException;

import java.util.HashMap;
import java.util.Map;

public class Minute {
    private static final Integer MINIMUM_TIME_UNIT = 5;
    private static final Map<Integer, Minute> cache = new HashMap<>();

    static {
        for (int i = 0; i < 24; i++) {
            Integer minutes = i * MINIMUM_TIME_UNIT;
            cache.put(minutes, new Minute(minutes));
        }
    }

    private final Integer minute;

    private Minute(final Integer minute) {
        if (minute < 0) {
            throw new MinusMinuteValueException();
        }
        this.minute = minute;
    }

    public static Minute from(final int minute) {
        return cache.getOrDefault(minute, new Minute(minute));
    }

    public static Minute from(final long minute) {
        return cache.getOrDefault((int) minute, new Minute((int) minute));
    }

    public boolean isNotDivisibleBy(final Minute that) {
        return this.minute % that.minute != 0;
    }

    public boolean isShorterThan(final Minute that) {
        return this.minute < that.minute;
    }
}
