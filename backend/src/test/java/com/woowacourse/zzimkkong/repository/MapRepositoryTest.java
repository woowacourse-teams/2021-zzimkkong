package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("page로 모든 맵을 조회한다.")
    void findAllByPaging() {
        // given
        Map save = maps.save(luther);

        // when
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.unsorted());
        Page<Map> actual = maps.findAllByFetch(pageRequest);

        // then
        assertThat(actual.getSize()).isEqualTo(20);
        assertThat(actual.getContent()).hasSize(1);
        assertThat(actual.getContent().get(0)).usingRecursiveComparison()
                .isEqualTo(save);
    }

    @Test
    @DisplayName("Fetch Join으로 공간을 같이 가져온다.")
    void findByIdFetchJoinSpace() {
        // given
        Map savedMap = maps.save(luther);

        Setting beSetting = Setting.builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Space be = Space.builder()
                .name(BE_NAME)
                .color(BE_COLOR)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(beSetting)
                .map(luther)
                .build();

        Setting feSetting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();

        Space fe = Space.builder()
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .description(FE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(feSetting)
                .build();

        spaces.save(be);
        spaces.save(fe);

        // when
        Map actual = maps.findByIdFetch(savedMap.getId())
                .orElseThrow(NoSuchMapException::new);

        // then
        assertThat(actual).isEqualTo(savedMap);
    }

    @Test
    @DisplayName("공간이 없어도 Fetch Join을 통해 Map을 찾아올 수 있다.")
    void findByIdFetchWithoutSpaces() {
        // given
        Map savedMap = maps.save(luther);

        // when
        Map actual = maps.findByIdFetch(savedMap.getId())
                .orElseThrow(NoSuchMapException::new);

        // then
        assertThat(actual).isEqualTo(savedMap);
    }
}
