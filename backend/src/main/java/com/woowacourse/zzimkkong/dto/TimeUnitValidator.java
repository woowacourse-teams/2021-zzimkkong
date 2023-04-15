package com.woowacourse.zzimkkong.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.woowacourse.zzimkkong.domain.TimeUnit.INTERVAL_TIME_UNITS;

public class TimeUnitValidator implements ConstraintValidator<TimeUnit, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if(value == null){
            return true;
        }

        return INTERVAL_TIME_UNITS.contains(value);
    }
}
