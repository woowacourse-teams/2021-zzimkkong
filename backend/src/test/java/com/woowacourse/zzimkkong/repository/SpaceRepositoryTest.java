package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class SpaceRepositoryTest extends RepositoryTest {
    private Map luther;
    private Space be;
    private Space fe;
    private Setting beSetting;

    @BeforeEach
    void setUp() {
        Member pobi = Member.builder()
                .email(EMAIL)
                .userName(USER_NAME)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);

        beSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        be = Space.builder()
                .name(BE_NAME)
                .color(BE_COLOR)
                .area(SPACE_DRAWING)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .spaceSettings(new Settings(List.of(beSetting)))
                .map(luther)
                .build();

        Setting feSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();

        fe = Space.builder()
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .spaceSettings(new Settings(List.of(feSetting)))
                .build();

        members.save(pobi);
        maps.save(luther);
    }

    @Test
    @DisplayName("공간을 저장한다.")
    void save() {
        // given, when
        Space savedSpace = spaces.save(be);

        // then
        assertThat(savedSpace.getId()).isNotNull();
        assertThat(savedSpace).isEqualTo(be);
    }

    @Test
    @DisplayName("page로 모든 공간을 조회한다.")
    void findAllByPaging() {
        // given
        Space savedBe = spaces.save(be);
        Space savedFe = spaces.save(fe);

        // when
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.unsorted());
        Page<Space> actual = spaces.findAllByFetch(pageRequest);

        // then
        assertThat(actual.getSize()).isEqualTo(20);
        assertThat(actual.getContent()).hasSize(2);
        assertThat(actual.getContent()).usingRecursiveComparison()
                .isEqualTo(List.of(savedBe, savedFe));
    }
}
