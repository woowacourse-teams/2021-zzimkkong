package com.woowacourse.zzimkkong.infrastructure.datetime;

import java.time.LocalDateTime;
import java.util.TimeZone;

public class TimeZoneUtils {
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    public static final TimeZone KST = TimeZone.getTimeZone("Asia/Seoul");
    public static final Long ONE_DAY_OFFSET = 1L;

    public static LocalDateTime convert(
            final LocalDateTime dateTime,
            final TimeZone fromTimeZone,
            final TimeZone toTimeZone) {
        return dateTime.atZone(fromTimeZone.toZoneId())
                .withZoneSameInstant(toTimeZone.toZoneId())
                .toLocalDateTime();
    }
}
