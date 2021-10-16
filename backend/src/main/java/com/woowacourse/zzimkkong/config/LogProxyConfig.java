package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LogProxyConfig {
    private static final String GROUP_NAME_OF_REPOSITORY = "repository";

    @Bean(name = "memberRepositoryProxy")
    @Primary
    public MemberRepository memberRepository(MemberRepository memberRepository) {
        return LogAspect.createLogProxy(memberRepository, MemberRepository.class, GROUP_NAME_OF_REPOSITORY);
    }

    @Bean(name = "mapRepositoryProxy")
    @Primary
    public MapRepository mapRepository(MapRepository mapRepository) {
        return LogAspect.createLogProxy(mapRepository, MapRepository.class, GROUP_NAME_OF_REPOSITORY);
    }

    @Bean(name = "spaceRepositoryProxy")
    @Primary
    public SpaceRepository spaceRepository(SpaceRepository spaceRepository) {
        return LogAspect.createLogProxy(spaceRepository, SpaceRepository.class, GROUP_NAME_OF_REPOSITORY);
    }

    @Bean(name = "reservationRepositoryProxy")
    @Primary
    public ReservationRepository reservationRepository(ReservationRepository reservationRepository) {
        return LogAspect.createLogProxy(reservationRepository, ReservationRepository.class, GROUP_NAME_OF_REPOSITORY);
    }
}
