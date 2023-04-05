package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.domain.Setting.FLAT_PRIORITY_ORDER;
import static com.woowacourse.zzimkkong.domain.Setting.FLAT_SETTING_ID;
import static org.assertj.core.api.Assertions.assertThat;

class SettingsTest {
    private Setting setting1;
    private Setting setting2;
    private Setting setting3;
    private Settings settings;

    @BeforeEach
    void setUp() {
        setting1 = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        LocalTime.of(10, 0),
                        LocalTime.of(13, 0)))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .priorityOrder(0)
                .build();
        setting2 = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        LocalTime.of(14, 0),
                        LocalTime.of(16, 0)))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .priorityOrder(1)
                .build();
        setting3 = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        LocalTime.of(16, 0),
                        LocalTime.of(20, 0)))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .priorityOrder(2)
                .build();

        settings = Settings.toPrioritizedSettings(List.of(setting1, setting2, setting3));
    }

    @ParameterizedTest
    @DisplayName("예약한 시간대와 요일이 주어질 때, 해당 예약과 관련된 예약 조건들만 뽑아온다")
    @MethodSource("provideArgumentsForGetSettingsByTimeSlotAndDayOfWeek")
    void getSettingsByTimeSlotAndDayOfWeek(TimeSlot reservationTimeSlot, Settings expectedResult) {
        Settings settings = this.settings.getSettingsByTimeSlotAndDayOfWeek(reservationTimeSlot, DayOfWeek.FRIDAY);
        assertThat(settings).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @DisplayName("예약 가능한 시간대에 속한 예약이면 false, 아니면 true")
    @MethodSource("provideArgumentsForCannotAcceptDueToAvailableTime")
    void cannotAcceptDueToAvailableTime(TimeSlot reservationTimeSlot, boolean expectedResult) {
        boolean result = this.settings.cannotAcceptDueToAvailableTime(
                reservationTimeSlot,
                DayOfWeek.MONDAY);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("예약이 불가능한 시간대를 계산해서 반환한다")
    void getUnavailableTimeSlots() {
        List<TimeSlot> expectedResult = List.of(
                TimeSlot.of(LocalTime.MIN, LocalTime.of(10, 0)),
                TimeSlot.of(LocalTime.of(13, 0), LocalTime.of(14, 0)),
                TimeSlot.of(LocalTime.of(20, 0), TimeSlot.MAX_TIME)
        );

        assertThat(settings.getUnavailableTimeSlots(DayOfWeek.MONDAY)).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("겹쳐진 예약 조건들을 '동등한 우선순위를 가진 겹치지 않은 상태 (= flat 한 상태)' 로 변환한다")
    void flatten() {
        Settings settings = Settings.toPrioritizedSettings(List.of(
                Setting.builder()
                        .settingTimeSlot(TimeSlot.of(
                                LocalTime.of(11, 0),
                                LocalTime.of(12, 0)))
                        .reservationTimeUnit(TimeUnit.from(10))
                        .reservationMinimumTimeUnit(TimeUnit.from(10))
                        .reservationMaximumTimeUnit(TimeUnit.from(30))
                        .enabledDayOfWeek("monday,tuesday,wednesday")
                        .priorityOrder(0)
                        .build(),
                Setting.builder()
                        .settingTimeSlot(TimeSlot.of(
                                LocalTime.of(10, 0),
                                LocalTime.of(13, 0)))
                        .reservationTimeUnit(TimeUnit.from(10))
                        .reservationMinimumTimeUnit(TimeUnit.from(10))
                        .reservationMaximumTimeUnit(TimeUnit.from(30))
                        .enabledDayOfWeek("monday,tuesday,wednesday,thursday,friday,saturday,sunday")
                        .priorityOrder(1)
                        .build()
        ));

        settings.flatten();

        List<Setting> expected = List.of(
                Setting.builder()
                        .settingTimeSlot(TimeSlot.of(
                                LocalTime.of(10, 0),
                                LocalTime.of(11, 0)))
                        .reservationTimeUnit(TimeUnit.from(10))
                        .reservationMinimumTimeUnit(TimeUnit.from(10))
                        .reservationMaximumTimeUnit(TimeUnit.from(30))
                        .enabledDayOfWeek("monday,tuesday,wednesday,thursday,friday,saturday,sunday")
                        .priorityOrder(FLAT_PRIORITY_ORDER)
                        .build(),
                Setting.builder()
                        .settingTimeSlot(TimeSlot.of(
                                LocalTime.of(11, 0),
                                LocalTime.of(12, 0)))
                        .reservationTimeUnit(TimeUnit.from(10))
                        .reservationMinimumTimeUnit(TimeUnit.from(10))
                        .reservationMaximumTimeUnit(TimeUnit.from(30))
                        .enabledDayOfWeek("monday,tuesday,wednesday")
                        .priorityOrder(FLAT_PRIORITY_ORDER)
                        .build(),
                Setting.builder()
                        .settingTimeSlot(TimeSlot.of(
                                LocalTime.of(11, 0),
                                LocalTime.of(12, 0)))
                        .reservationTimeUnit(TimeUnit.from(10))
                        .reservationMinimumTimeUnit(TimeUnit.from(10))
                        .reservationMaximumTimeUnit(TimeUnit.from(30))
                        .enabledDayOfWeek("thursday,friday,saturday,sunday")
                        .priorityOrder(FLAT_PRIORITY_ORDER)
                        .build(),
                Setting.builder()
                        .settingTimeSlot(TimeSlot.of(
                                LocalTime.of(12, 0),
                                LocalTime.of(13, 0)))
                        .reservationTimeUnit(TimeUnit.from(10))
                        .reservationMinimumTimeUnit(TimeUnit.from(10))
                        .reservationMaximumTimeUnit(TimeUnit.from(30))
                        .enabledDayOfWeek("monday,tuesday,wednesday,thursday,friday,saturday,sunday")
                        .priorityOrder(FLAT_PRIORITY_ORDER)
                        .build()
        );

        assertThat(settings.getSettings()).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    private static Stream<Arguments> provideArgumentsForGetSettingsByTimeSlotAndDayOfWeek() {
        return Stream.of(
                Arguments.of(
                        TimeSlot.of(LocalTime.of(8, 0), LocalTime.of(10, 0)),
                        Settings.toFlattenedSettings(Collections.emptyList())),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(8, 0), LocalTime.of(11, 0)),
                        Settings.toFlattenedSettings(List.of(
                                Setting.builder()
                                        .id(FLAT_SETTING_ID)
                                        .settingTimeSlot(TimeSlot.of(
                                                LocalTime.of(10, 0),
                                                LocalTime.of(13, 0)))
                                        .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                                        .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                                        .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                                        .enabledDayOfWeek(EnabledDayOfWeek.FRIDAY.name().toLowerCase(Locale.ROOT))
                                        .priorityOrder(FLAT_PRIORITY_ORDER)
                                        .build()))),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(13, 0), LocalTime.of(14, 0)),
                        Settings.toFlattenedSettings(Collections.emptyList())),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(13, 30), LocalTime.of(15, 0)),
                        Settings.toFlattenedSettings(List.of(
                                Setting.builder()
                                        .id(FLAT_SETTING_ID)
                                        .settingTimeSlot(TimeSlot.of(
                                                LocalTime.of(14, 0),
                                                LocalTime.of(16, 0)))
                                        .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                                        .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                                        .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                                        .enabledDayOfWeek(EnabledDayOfWeek.FRIDAY.name().toLowerCase(Locale.ROOT))
                                        .priorityOrder(FLAT_PRIORITY_ORDER)
                                        .build()))),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(15, 0), LocalTime.of(23, 0)),
                        Settings.toFlattenedSettings(List.of(
                                Setting.builder()
                                        .id(FLAT_SETTING_ID)
                                        .settingTimeSlot(TimeSlot.of(
                                                LocalTime.of(14, 0),
                                                LocalTime.of(20, 0)))
                                        .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                                        .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                                        .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                                        .enabledDayOfWeek(EnabledDayOfWeek.FRIDAY.name().toLowerCase(Locale.ROOT))
                                        .priorityOrder(FLAT_PRIORITY_ORDER)
                                        .build()))));
    }

    private static Stream<Arguments> provideArgumentsForCannotAcceptDueToAvailableTime() {
        return Stream.of(
                Arguments.of(
                        TimeSlot.of(LocalTime.of(9, 0), LocalTime.of(11, 0)),
                        true),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                        false),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(13, 0), LocalTime.of(14, 0)),
                        true),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(13, 30), LocalTime.of(15, 0)),
                        true),
                Arguments.of(
                        TimeSlot.of(LocalTime.of(14, 0), LocalTime.of(18, 0)),
                        false));
    }
}

