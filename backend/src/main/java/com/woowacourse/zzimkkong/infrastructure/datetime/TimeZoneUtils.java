package com.woowacourse.zzimkkong.infrastructure.datetime;

import com.woowacourse.zzimkkong.domain.ServiceZone;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

public class TimeZoneUtils {
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static LocalDateTime convertTo(final LocalDateTime dateTime, final ServiceZone serviceZone) {
        return dateTime.atZone(UTC.toZoneId())
                .withZoneSameInstant(ZoneId.of(serviceZone.getTimeZone()))
                .toLocalDateTime();
    }

    public static LocalDateTime convertToUTC(final ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime();
    }
}
