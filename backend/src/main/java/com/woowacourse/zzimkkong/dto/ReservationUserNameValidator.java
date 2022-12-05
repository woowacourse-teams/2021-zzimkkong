package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.infrastructure.auth.AuthorizationExtractor;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.NAMING_FORMAT;

public class ReservationUserNameValidator implements ConstraintValidator<ReservationUserName, String> {
    @Autowired
    private HttpServletRequest request;

    @Override
    public void initialize(final ReservationUserName constraintAnnotation) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (AuthorizationExtractor.hasAccessToken(request)) {
            return true;
        }

        return StringUtils.isNotBlank(value) && value.matches(NAMING_FORMAT);
    }
}
