package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.config.logaspect.LogAspectConfigurer;
import com.woowacourse.zzimkkong.repository.*;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogAspectConfig extends LogAspectConfigurer {
    private static final String LOG_GROUP_NAME_OF_REPOSITORY = "repository";

    @Override
    protected void registerProxyBeans(LogProxyBeanRegistry logProxyBeanRegistry) {
        logProxyBeanRegistry.add(MemberRepository.class, LOG_GROUP_NAME_OF_REPOSITORY);
        logProxyBeanRegistry.add(MapRepository.class, LOG_GROUP_NAME_OF_REPOSITORY);
        logProxyBeanRegistry.add(SpaceRepository.class, LOG_GROUP_NAME_OF_REPOSITORY);
        logProxyBeanRegistry.add(ReservationRepository.class, LOG_GROUP_NAME_OF_REPOSITORY);
        logProxyBeanRegistry.add(PresetRepository.class, LOG_GROUP_NAME_OF_REPOSITORY);
    }
}
