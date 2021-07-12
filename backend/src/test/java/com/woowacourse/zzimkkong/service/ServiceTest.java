package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ServiceTest {
    @MockBean
    protected MemberRepository memberRepository;

    @MockBean
    protected ReservationRepository reservationRepository;

    @MockBean
    protected MapRepository mapRepository;

    @MockBean
    protected SpaceRepository spaceRepository;
}
