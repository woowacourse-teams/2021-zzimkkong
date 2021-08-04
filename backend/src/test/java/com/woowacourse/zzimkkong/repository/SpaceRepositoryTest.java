package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class SpaceRepositoryTest extends RepositoryTest {
    private Map luther;
    private Space be;
    private Space fe;

    @BeforeEach
    void setUp() {
        Member pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
        luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);

        Setting beSetting = new Setting.Builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .disabledWeekdays(BE_ENABLED_DAY_OF_WEEK)
                .build();

        be = new Space.Builder()
                .name(BE_NAME)
                .color(BE_COLOR)
                .map(luther)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(beSetting)
                .build();

        Setting feSetting = new Setting.Builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .disabledWeekdays(FE_ENABLED_DAY_OF_WEEK)
                .build();

        fe = new Space.Builder()
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .description(FE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(feSetting)
                .build();

        members.save(pobi);
        maps.save(luther);
    }

    @DisplayName("공간을 저장한다.")
    @Test
    void save() {
        // given, when
        Space savedSpace = spaces.save(be);

        // then
        assertThat(savedSpace.getId()).isNotNull();
        assertThat(savedSpace).isEqualTo(be);
    }

    @DisplayName("맵의 Id를 이용해 모든 공간을 찾아온다.")
    @Test
    void findAllByMapId() {
        // given
        spaces.save(be);
        spaces.save(fe);

        // when
        List<Space> actual = spaces.findAllByMapId(luther.getId());

        // then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(List.of(be, fe));
    }
}
