package com.woowacourse.zzimkkong.config.warmup;

import com.woowacourse.zzimkkong.infrastructure.warmup.Warmer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
@Profile("!test")
public class WarmUpScheduler {
    private Warmer warmer;

    public WarmUpScheduler(final Warmer warmer) {
        this.warmer = warmer;
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void warmUp() {
        warmer.warmUp();
    }
}
