package com.woowacourse.zzimkkong.infrastructure;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Profile({"test", "local"})
public class LocalTimeConverter implements TimeConverter {
    @Override
    public ZonedDateTime getNow() {
        LocalDateTime localNow = LocalDateTime.now();
        return localNow.atZone(ZoneId.of("Asia/Seoul"));
    }
}
