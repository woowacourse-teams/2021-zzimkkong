package com.woowacourse.zzimkkong.infrastructure;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface TimeConverter {
    ZonedDateTime getNow();
    default ZonedDateTime convertTimeZone(final LocalDateTime startDateTime) {
        return startDateTime.atZone(ZoneId.of("Asia/Seoul"));
    }
}
