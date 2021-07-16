package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.woowacourse.zzimkkong.CommonFixture.LUTHER;
import static org.assertj.core.api.Assertions.assertThat;

class MapRepositoryTest extends RepositoryTest {
    @Test
    @DisplayName("Map을 저장한다.")
    void save() {
        //given, when
        Map savedMap = maps.save(LUTHER);

        //then
        assertThat(savedMap.getId()).isNotNull();
    }

    @Test
    @DisplayName("id로부터 저장된 Map을 찾아올 수 있다.")
    void findById() {
        //given
        Map savedMap = maps.save(LUTHER);

        //when
        Map findMap = maps.findById(savedMap.getId())
                .orElseThrow(NoSuchMapException::new);

        //then
        assertThat(findMap).isEqualTo(savedMap);
    }
}
