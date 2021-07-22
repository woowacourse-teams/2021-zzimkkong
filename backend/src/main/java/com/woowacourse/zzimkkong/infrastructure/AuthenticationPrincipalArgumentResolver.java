package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
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
    private final MemberRepository members;

    public AuthenticationPrincipalArgumentResolver(final JwtUtils jwtUtils, final MemberRepository members) {
        this.jwtUtils = jwtUtils;
        this.members = members;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Manager.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String token = AuthorizationExtractor.extractAccessToken((HttpServletRequest) webRequest.getNativeRequest());
        String email = jwtUtils.getPayload(token);
        return members.findByEmail(email).orElseThrow(NoSuchMemberException::new);
    }
}
