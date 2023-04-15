package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class SpaceTest {
    private static final Setting setting1 = Setting.builder()
            .settingTimeSlot(TimeSlot.of(
                    LocalTime.of(10, 0),
                    LocalTime.of(14, 0)))
            .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
            .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
            .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
            .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
            .priorityOrder(0)
            .build();
    private static final Setting setting2 = Setting.builder()
            .settingTimeSlot(TimeSlot.of(
                    LocalTime.of(15, 0),
                    LocalTime.of(18, 0)))
            .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
            .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
            .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
            .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
            .priorityOrder(1)
            .build();

    @Test
    void update() {
        Member member = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        Map map = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, member);

        Setting setting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        LocalTime.of(10, 0),
                        LocalTime.of(18, 0)))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .priorityOrder(0)
                .build();
        Settings settings = Settings.toPrioritizedSettings(Arrays.asList(setting));
        Space space = Space.builder()
                .name("와우")
                .color("색깔입니다")
                .area("area")
                .reservationEnable(FE_RESERVATION_ENABLE)
                .spaceSettings(settings)
                .map(map)
                .build();

        Setting updateSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        LocalTime.of(10, 0),
                        LocalTime.of(18, 0)))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .priorityOrder(0)
                .build();
        Settings updateSettings = Settings.toPrioritizedSettings(Arrays.asList(setting));
        Space updateSpace = Space.builder()
                .name("우와")
                .color("색깔")
                .area("area")
                .reservationEnable(false)
                .spaceSettings(updateSettings)
                .map(map)
                .build();

        space.update(updateSpace);
        assertThat(space).usingRecursiveComparison().isEqualTo(updateSpace);
    }

    @Test
    @DisplayName("공간의 Id가 동일한 Id면 true, 아니면 false를 반환한다")
    void hasSameId() {
        Space space = Space.builder()
                .id(1L)
                .build();

        assertThat(space.hasSameId(space.getId())).isTrue();
        assertThat(space.hasSameId(space.getId() + 1)).isFalse();
    }

    @Test
    @DisplayName("예약이 가능한 공간이면 false를 반환한다")
    void isUnableToReserve() {
        Setting reservationEnableSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .priorityOrder(0)
                .build();
        Settings settings = Settings.toPrioritizedSettings(Arrays.asList(reservationEnableSetting));
        Space reservationEnableSpace = Space.builder()
                .spaceSettings(settings)
                .reservationEnable(true)
                .build();

        assertThat(reservationEnableSpace.isUnableToReserve()).isFalse();
    }

    @Test
    @DisplayName("예약이 불가능한 공간이면 true를 반환한다")
    void isUnableToReserveFail() {
        Setting reservationUnableSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .priorityOrder(0)
                .build();
        Settings settings = Settings.toPrioritizedSettings(Arrays.asList(reservationUnableSetting));
        Space reservationUnableSpace = Space.builder()
                .spaceSettings(settings)
                .reservationEnable(false)
                .build();

        assertThat(reservationUnableSpace.isUnableToReserve()).isTrue();
    }

    @ParameterizedTest
    @DisplayName("예약하려는 시간과 요일에 부합하는 조건을 반환한다")
    @MethodSource("provideReservationInfo")
    void isNotBetweenAvailableTime(TimeSlot reservationTimeSlot, DayOfWeek dayofWeek, Settings expectedResult) {
        // setting1: 10 ~ 14
        // setting2: 15 ~ 18
        Space space = Space.builder()
                .spaceSettings(Settings.toPrioritizedSettings(Arrays.asList(setting1, setting2)))
                .build();

        Settings relevantSettings = space.getRelevantSettings(reservationTimeSlot, dayofWeek);
        assertThat(relevantSettings).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideReservationInfo() {
        return Stream.of(
                Arguments.of(
                        TimeSlot.of(
                                LocalTime.of(9, 0),
                                LocalTime.of(10, 0)),
                        DayOfWeek.MONDAY,
                        new Settings()),
                Arguments.of(
                        TimeSlot.of(
                                LocalTime.of(10, 0),
                                LocalTime.of(12, 0)),
                        DayOfWeek.MONDAY,
                        Settings.toFlattenedSettings(List.of(setting1)).getMergedSettings(EnabledDayOfWeek.MONDAY)),
                Arguments.of(
                        TimeSlot.of(
                                LocalTime.of(12, 0),
                                LocalTime.of(15, 0)),
                        DayOfWeek.MONDAY,
                        Settings.toFlattenedSettings(List.of(setting1)).getMergedSettings(EnabledDayOfWeek.MONDAY)),
                Arguments.of(
                        TimeSlot.of(
                                LocalTime.of(14, 0),
                                LocalTime.of(15, 0)),
                        DayOfWeek.MONDAY,
                        new Settings()),
                Arguments.of(
                        TimeSlot.of(
                                LocalTime.of(15, 0),
                                LocalTime.of(19, 0)),
                        DayOfWeek.MONDAY,
                        Settings.toFlattenedSettings(List.of(setting2)).getMergedSettings(EnabledDayOfWeek.MONDAY)));
    }
}
