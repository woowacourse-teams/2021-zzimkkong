package com.woowacourse.zzimkkong.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.woowacourse.zzimkkong.domain.Space.DELIMITER;

public class DayOfWeekValidator implements ConstraintValidator<DayOfWeekConstraint, String> {
    @Override
    public void initialize(final DayOfWeekConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        List<String> dayOfWeekInput = Arrays.stream(value.split(DELIMITER))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        if (hasDuplicates(dayOfWeekInput)) {
            return false;
        }

        return isValidDayOfWeekName(dayOfWeekInput);
    }

    private boolean hasDuplicates(List<String> dayOfWeekInput) {
        Set<String> uniqueDayOfWeekInput = new HashSet<>(dayOfWeekInput);
        return uniqueDayOfWeekInput.size() != dayOfWeekInput.size();
    }

    private Boolean isValidDayOfWeekName(final List<String> dayOfWeekInput) {
        List<String> allDaysOfWeekNames = Arrays.stream(DayOfWeek.values())
                .map(DayOfWeek::name)
                .collect(Collectors.toList());

        return allDaysOfWeekNames.containsAll(dayOfWeekInput);
    }
}
