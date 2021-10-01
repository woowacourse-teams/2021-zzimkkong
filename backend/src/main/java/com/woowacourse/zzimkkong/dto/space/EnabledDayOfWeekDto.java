package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.woowacourse.zzimkkong.exception.dto.EnabledDayOfWeekResponseReflectionException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.woowacourse.zzimkkong.domain.Space.DELIMITER;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnabledDayOfWeekDto {
    private Boolean monday = true;
    private Boolean tuesday = true;
    private Boolean wednesday = true;
    private Boolean thursday = true;
    private Boolean friday = true;
    private Boolean saturday = true;
    private Boolean sunday = true;

    public static EnabledDayOfWeekDto from(final String enabledDayOfWeekString) {
        final EnabledDayOfWeekDto enabledDayOfWeekDto = new EnabledDayOfWeekDto();
        setFields(enabledDayOfWeekDto, enabledDayOfWeekString);
        return enabledDayOfWeekDto;
    }

    private static void setFields(final EnabledDayOfWeekDto enabledDayOfWeekDto, final String enabledDayOfWeekString) {
        final Field[] fields = enabledDayOfWeekDto.getClass().getDeclaredFields();
        final List<String> enabledFieldNames = getEnabledFieldNames(enabledDayOfWeekString);

        Arrays.stream(fields)
                .filter(field -> !enabledFieldNames.contains(field.getName()))
                .forEach(field -> setFalse(enabledDayOfWeekDto, field));
    }

    private static List<String> getEnabledFieldNames(final String enabledDayOfWeekString) {
        return Arrays.stream(enabledDayOfWeekString.split(DELIMITER))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private static void setFalse(final EnabledDayOfWeekDto enabledDayOfWeekDto, final Field targetField) {
        try {
            targetField.setAccessible(true);
            targetField.set(enabledDayOfWeekDto, Boolean.FALSE);
        } catch (IllegalAccessException e) {
            throw new EnabledDayOfWeekResponseReflectionException();
        }
    }

    @Override
    public String toString() {
        final Field[] fields = this.getClass().getDeclaredFields();
        final List<String> enabledDayOfWeek = getEnabledDayOfWeek(fields);
        return String.join(DELIMITER, enabledDayOfWeek);
    }

    private List<String> getEnabledDayOfWeek(final Field[] fields) {
        final List<String> enabledDayOfWeek = new ArrayList<>();
        for (final Field field : fields) {
            field.setAccessible(true);
            final Boolean value = getValue(field);
            if (Boolean.TRUE.equals(value)) {
                enabledDayOfWeek.add(field.getName());
            }
        }
        return enabledDayOfWeek;
    }

    private Boolean getValue(final Field field) {
        try {
            return (Boolean) field.get(this);
        } catch (IllegalAccessException e) {
            throw new EnabledDayOfWeekResponseReflectionException();
        }
    }
}
