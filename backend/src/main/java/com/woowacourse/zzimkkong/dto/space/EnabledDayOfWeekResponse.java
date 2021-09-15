package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.exception.dto.EnabledDayOfWeekResponseSetException;
import com.woowacourse.zzimkkong.exception.space.NoSuchDayOfWeekException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.Arrays;

@Getter
@NoArgsConstructor
public class EnabledDayOfWeekResponse {
    private Boolean monday = false;
    private Boolean tuesday = false;
    private Boolean wednesday = false;
    private Boolean thursday = false;
    private Boolean friday = false;
    private Boolean saturday = false;
    private Boolean sunday = false;

    public static EnabledDayOfWeekResponse from(final String enabledDayOfWeek) {
        final EnabledDayOfWeekResponse enabledDayOfWeekResponse = new EnabledDayOfWeekResponse();

        final Field[] declaredFields = enabledDayOfWeekResponse.getClass().getDeclaredFields();
        Arrays.stream(enabledDayOfWeek.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .forEach(dayOfWeek -> {
                    final Field targetField = findFieldByDayOfWeek(declaredFields, dayOfWeek);
                    setField(enabledDayOfWeekResponse, targetField);
                });

        return enabledDayOfWeekResponse;
    }

    private static Field findFieldByDayOfWeek(final Field[] declaredFields, final String dayOfWeek) {
        return Arrays.stream(declaredFields)
                .filter(field -> dayOfWeek.equals(field.getName()))
                .findFirst()
                .orElseThrow(NoSuchDayOfWeekException::new);
    }

    private static void setField(final EnabledDayOfWeekResponse enabledDayOfWeekResponse, final Field targetField) {
        try {
            targetField.setAccessible(true);
            targetField.set(enabledDayOfWeekResponse, Boolean.TRUE);
        } catch (IllegalAccessException e) {
            throw new EnabledDayOfWeekResponseSetException();
        }
    }
}
