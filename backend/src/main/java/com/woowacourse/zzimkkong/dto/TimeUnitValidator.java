package com.woowacourse.zzimkkong.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class TimeUnitValidator implements ConstraintValidator<TimeUnit, Integer> {
    private final List<Integer> TIME_UNITS = List.of(5, 10, 30, 60, 120);

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if(value == null){
            return true;
        }

        return TIME_UNITS.contains(value);
    }
}
