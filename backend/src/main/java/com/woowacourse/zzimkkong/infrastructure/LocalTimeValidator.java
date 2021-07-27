package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.exception.reservation.ImpossibleStartTimeException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Profile({"test", "local"})
public class LocalTimeValidator implements TimeValidator {
    @Override
    public void validateStartTimeInPast(final LocalDateTime startDateTime) {
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedLocal = localNow.atZone(ZoneId.of("Asia/Seoul"));

        ZonedDateTime zonedStartDateTime = startDateTime.atZone(ZoneId.of("Asia/Seoul"));

        if (zonedStartDateTime.isBefore(zonedLocal)) {
            throw new ImpossibleStartTimeException();
        }
    }
}
