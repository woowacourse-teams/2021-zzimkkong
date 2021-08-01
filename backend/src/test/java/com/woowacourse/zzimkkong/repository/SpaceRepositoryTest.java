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

    @DisplayName("공간을 저장한다.")
    @Test
    void save() {
        // given, when
        Space savedSpace = spaces.save(BE);

        // then
        assertThat(savedSpace.getId()).isNotNull();
        assertThat(savedSpace).usingRecursiveComparison()
                .isEqualTo(BE);
    }

    @DisplayName("맵의 Id를 이용해 모든 공간을 찾아온다.")
    @Test
    void findAllByMapId() {
        // given
        spaces.save(BE);
        spaces.save(FE1);

        // when
        List<Space> actual = spaces.findAllByMapId(LUTHER.getId());

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(List.of(BE, FE1));
    }
}
