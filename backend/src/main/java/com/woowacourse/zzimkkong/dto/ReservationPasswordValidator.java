package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.infrastructure.auth.AuthorizationExtractor;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.RESERVATION_PW_FORMAT;

public class ReservationPasswordValidator implements ConstraintValidator<ReservationPassword, String> {
    @Autowired
    private HttpServletRequest request;

    @Override
    public void initialize(final ReservationPassword constraintAnnotation) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (AuthorizationExtractor.hasAccessToken(request)) {
            return true;
        }

        return StringUtils.isNotBlank(value) && value.matches(RESERVATION_PW_FORMAT);
    }
}
