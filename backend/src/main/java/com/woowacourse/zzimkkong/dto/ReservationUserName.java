package com.woowacourse.zzimkkong.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.NAME_MESSAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ReservationUserNameValidator.class)
@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Documented
public @interface ReservationUserName {
    String message() default NAME_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
