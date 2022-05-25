package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.IllegalTimeUnitValueException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TimeUnit {
    public static final Integer MINIMUM_TIME_UNIT = 5;
    private static final Integer MINIMUM_TIME = 0;
    private static final Map<Integer, TimeUnit> cache = new ConcurrentHashMap<>();

    static {
        for (int i = 1; i <= 24; i++) {
            Integer minutes = i * MINIMUM_TIME_UNIT;
            cache.put(minutes, new TimeUnit(minutes));
        }
    }

    @Column(nullable = false)
    private Integer minutes;

    private TimeUnit(final Integer minutes) {
        this.minutes = minutes;

        if (this.minutes <= MINIMUM_TIME || cannotDivideByMinimumTimeUnit(this.minutes)) {
            throw new IllegalTimeUnitValueException(this.minutes);
        }
    }

    public static TimeUnit from(final int minutes) {
        return cache.getOrDefault(minutes, new TimeUnit(minutes));
    }

    public static TimeUnit from(final long minutes) {
        return cache.getOrDefault((int) minutes, new TimeUnit((int) minutes));
    }

    public static boolean cannotDivideByMinimumTimeUnit(final int minutes) {
        return minutes % MINIMUM_TIME_UNIT != 0;
    }

    public boolean isDivisibleBy(final TimeUnit that) {
        return this.minutes % that.minutes == 0;
    }

    public boolean canDivide(final LocalTime time) {
        return time.getMinute() % this.minutes == 0;
    }

    public boolean isShorterThan(final TimeUnit that) {
        return this.minutes < that.minutes;
    }
}
