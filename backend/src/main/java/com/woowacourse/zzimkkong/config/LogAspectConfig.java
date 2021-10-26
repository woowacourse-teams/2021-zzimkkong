package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.config.logaspect.LogAspectConfigurer;
import com.woowacourse.zzimkkong.repository.*;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogAspectConfig extends LogAspectConfigurer {
    private static final String LOG_GROUP_NAME_OF_REPOSITORY = "repository";

    @Override
    protected void registerBeans(LogRegistry logRegistry) {
        logRegistry.add(MemberRepository.class, LOG_GROUP_NAME_OF_REPOSITORY);
        logRegistry.add(MapRepository.class, LOG_GROUP_NAME_OF_REPOSITORY);
        logRegistry.add(SpaceRepository.class, LOG_GROUP_NAME_OF_REPOSITORY);
        logRegistry.add(ReservationRepository.class, LOG_GROUP_NAME_OF_REPOSITORY);
        logRegistry.add(PresetRepository.class, LOG_GROUP_NAME_OF_REPOSITORY);
    }
}
