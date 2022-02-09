package com.woowacourse.zzimkkong.infrastructure.datetime;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.TimeZone;

public class TimeZoneUtils {
	public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
	
	public static ZonedDateTime addTimeZone(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return ZonedDateTime.of(dateTime, DEFAULT_TIMEZONE.toZoneId());
	}
}
