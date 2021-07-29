package com.woowacourse.zzimkkong.infrastructure;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Profile("prod")
public class ProductionTimeConverter implements TimeConverter {
    @Override
    public LocalDateTime getNow() {
        LocalDateTime localNow = LocalDateTime.now();
        return localNow.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();
    }
}
