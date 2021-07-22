package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.woowacourse.zzimkkong.CommonFixture.LUTHER;
import static com.woowacourse.zzimkkong.CommonFixture.POBI;
import static com.woowacourse.zzimkkong.service.ServiceTestFixture.SMALL_HOUSE;
import static org.assertj.core.api.Assertions.assertThat;

class MapRepositoryTest extends RepositoryTest {
    @BeforeEach
    void setUp() {
        members.save(POBI);
    }

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

    @Test
    @DisplayName("Member의 모든 맵을 조회할 수 있다.")
    void findAllByMember() {
        //given
        Map savedMap1 = maps.save(LUTHER);
        Map savedMap2 = maps.save(SMALL_HOUSE);

        //when
        List<Map> actualMaps = maps.findAllByMember(POBI);

        //then
        assertThat(actualMaps).isEqualTo(List.of(savedMap1, savedMap2));
    }
}
