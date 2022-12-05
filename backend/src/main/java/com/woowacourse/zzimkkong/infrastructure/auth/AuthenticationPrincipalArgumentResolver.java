package com.woowacourse.zzimkkong.infrastructure.auth;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
@LogMethodExecutionTime(group = "infrastructure")
public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtUtils jwtUtils;

    public AuthenticationPrincipalArgumentResolver(final JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(com.woowacourse.zzimkkong.domain.LoginEmail.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        com.woowacourse.zzimkkong.domain.LoginEmail loginEmailParameterAnnotation = parameter.getParameterAnnotation(com.woowacourse.zzimkkong.domain.LoginEmail.class);
        if (loginEmailParameterAnnotation.isOptional() && !AuthorizationExtractor.hasAccessToken(request)) {
            return LoginUserEmail.NO_LOGIN;
        }

        String token = AuthorizationExtractor.extractAccessToken(request);
        String email = jwtUtils.getPayload(token);
        return LoginUserEmail.from(email);
    }
}
