package com.woowacourse.zzimkkong.dto;

import org.junit.jupiter.api.BeforeAll;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class RequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    Set<ConstraintViolation<Object>> getConstraintViolations(Object object) {
        return validator.validate(object);
    }
}
