package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class SpaceRepositoryTest extends RepositoryTest {
    @BeforeEach
    void setUp() {
        members.save(POBI);
        maps.save(LUTHER);
    }

    @Test
    @DisplayName("맵의 Id를 이용해 모든 공간을 찾아온다.")
    void findAllByMapId() {
        // given
        spaces.save(BE);
        spaces.save(FE1);

        // when
        List<Space> actual = spaces.findAllByMapId(LUTHER.getId());

        // then
        assertThat(actual).isEqualTo(List.of(BE, FE1));
    }
}
