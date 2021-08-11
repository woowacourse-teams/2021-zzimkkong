package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class MapRepositoryTest extends RepositoryTest {
    private Member pobi;
    private Map luther;
    private Map smallHouse;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, PW, ORGANIZATION);
        luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);
        smallHouse = new Map(SMALL_HOUSE_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);

        members.save(pobi);
    }

    @Test
    @DisplayName("Map을 저장한다.")
    void save() {
        //given, when
        Map savedMap = maps.save(luther);

        //then
        assertThat(savedMap.getId()).isNotNull();
        assertThat(savedMap).isEqualTo(luther);
    }

    @Test
    @DisplayName("id로부터 저장된 Map을 찾아올 수 있다.")
    void findById() {
        //given
        Map savedMap = maps.save(luther);

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
        Map savedMap1 = maps.save(luther);
        Map savedMap2 = maps.save(smallHouse);

        //when
        List<Map> actualMaps = maps.findAllByMember(pobi);

        //then
        assertThat(actualMaps).containsExactlyInAnyOrderElementsOf(List.of(savedMap1, savedMap2));
    }
}
