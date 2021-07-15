package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class RepositoryTest {
    @Autowired
    protected ReservationRepository reservationRepository;
    @Autowired
    protected SpaceRepository spaces;
    @Autowired
    protected MemberRepository members;
    @Autowired
    protected MapRepository maps;

    @BeforeEach
    void setUp() {
        DataLoader dataLoader = new DataLoader(
                members,
                maps,
                spaces,
                reservationRepository
        );

        dataLoader.run();
    }
}
