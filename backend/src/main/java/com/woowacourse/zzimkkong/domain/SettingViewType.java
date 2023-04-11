package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import org.springframework.http.HttpStatus;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * {@link SettingViewType#STACK_SINGLE}: 맵 관리자가 설정한 특정 요일의 예약 조건 그대로 보여준다 (겹치는 조건 O). 관리자들을 위한 view
 * {@link SettingViewType#STACK_ALL}: 맵 관리자가 설정한 모든 요일의 예약 조건 그대로 보여준다 (겹치는 조건 O). 관리자들을 위한 view
 * {@link SettingViewType#FLAT_SINGLE}: 특정 요일의 예약 조건을 평탄화 하여 보여준다 (겹치는 조건 X). 예약자들을 위한 view
 * {@link SettingViewType#FLAT_ALL}: 모든 요일의 예약 조건을 평탄화 하여 보여준다 (겹치는 조건 X). 예약자들을 위한 view
 * - {@link com.woowacourse.zzimkkong.domain.Settings#flatten()} 메서드 참고
 */
public enum SettingViewType {
    FLAT_SINGLE(
            (dateTime, viewType) -> dateTime != null && "FLAT".equals(viewType),
            SettingViewType::getFlatSingleSummary),
    STACK_SINGLE(
            (dateTime, viewType) -> dateTime != null && "STACK".equals(viewType),
            SettingViewType::getStackSingleSummary),
    FLAT_ALL(
            (dateTime, viewType) -> dateTime == null && "FLAT".equals(viewType),
            SettingViewType::getFlatAllSummary),
    STACK_ALL(
            (dateTime, viewType) -> dateTime == null && "STACK".equals(viewType),
            SettingViewType::getStackAllSummary);

    private final BiPredicate<LocalDateTime, String> expression;
    private final BiFunction<Space, LocalDateTime, String> function;

    SettingViewType(final BiPredicate<LocalDateTime, String> expression, final BiFunction<Space, LocalDateTime, String> function) {
        this.expression = expression;
        this.function = function;
    }

    public static SettingViewType of(final LocalDateTime dateTime, final String viewType) {
        return Arrays.stream(values())
                .filter(settingViewType -> settingViewType.expression.test(dateTime, viewType))
                .findFirst()
                .orElseThrow(() -> new ZzimkkongException(HttpStatus.BAD_REQUEST));
    }

    public String getSummary(final Space space, final LocalDateTime dateTime) {
        return function.apply(space, dateTime);
    }

    private static String getFlatSingleSummary(final Space space, final LocalDateTime dateTime) {
        Settings spaceSettings = space.getSpaceSettings();
        DayOfWeek dayOfWeek = TimeZoneUtils.convertTo(dateTime, space.getMap().getServiceZone())
                .toLocalDate()
                .getDayOfWeek();

        spaceSettings.flatten();

        return spaceSettings.getSummaryOn(EnabledDayOfWeek.from(dayOfWeek.name()));
    }

    private static String getStackSingleSummary(final Space space, final LocalDateTime dateTime) {
        Settings spaceSettings = space.getSpaceSettings();
        DayOfWeek dayOfWeek = TimeZoneUtils.convertTo(dateTime, space.getMap().getServiceZone())
                .toLocalDate()
                .getDayOfWeek();

        return spaceSettings.getSummaryOn(EnabledDayOfWeek.from(dayOfWeek.name()));
    }

    private static String getFlatAllSummary(final Space space, final LocalDateTime dateTime) {
        Settings spaceSettings = space.getSpaceSettings();

        spaceSettings.flatten();

        return spaceSettings.getSummary();
    }

    private static String getStackAllSummary(final Space space, final LocalDateTime dateTime) {
        return space.getSpaceSettings().getSummary();
    }
}
