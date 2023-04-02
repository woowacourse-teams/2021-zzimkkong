package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.setting.NoSuchEnabledDayOfWeekException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Getter
public enum EnabledDayOfWeek {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일");

    private final String displayName;

    EnabledDayOfWeek(final String displayName) {
        this.displayName = displayName;
    }

    public static String getDisplayNames(String enabledDayOfWeek) {
        return Arrays.stream(enabledDayOfWeek.split(Space.DELIMITER))
                .map(String::trim)
                .map(EnabledDayOfWeek::from)
                .map(dayOfWeek -> dayOfWeek.displayName)
                .collect(Collectors.joining(Space.DELIMITER));
    }

    public static EnabledDayOfWeek from(String dayOfWeek) {
        return Arrays.stream(values())
                .filter(enabledDayOfWeek -> enabledDayOfWeek.name().equals(dayOfWeek.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 요일입니다. {}", dayOfWeek);
                    throw new NoSuchEnabledDayOfWeekException();
                });
    }
}

