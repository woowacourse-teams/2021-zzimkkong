package com.woowacourse.zzimkkong.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.woowacourse.zzimkkong.dto.Validator.DAY_OF_WEEK_MESSAGE;

@Documented
@Constraint(validatedBy = DayOfWeekValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DayOfWeekConstraint {
    String message() default DAY_OF_WEEK_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
