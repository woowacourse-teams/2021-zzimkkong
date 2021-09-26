package com.woowacourse.zzimkkong.infrastructure.auth;

import com.woowacourse.zzimkkong.domain.Manager;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtUtils jwtUtils;

    public AuthenticationPrincipalArgumentResolver(final JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Manager.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String token = AuthorizationExtractor.extractAccessToken((HttpServletRequest) webRequest.getNativeRequest());
        String email = jwtUtils.getPayload(token);
        return LoginEmail.from(email);
    }
}
