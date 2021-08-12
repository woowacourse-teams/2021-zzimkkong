package com.woowacourse.zzimkkong.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class RepositoryTest {

    @Autowired
    protected ReservationRepository reservations;

    @Autowired
    protected SpaceRepository spaces;

    @Autowired
    protected MemberRepository members;

    @Autowired
    protected MapRepository maps;
}
