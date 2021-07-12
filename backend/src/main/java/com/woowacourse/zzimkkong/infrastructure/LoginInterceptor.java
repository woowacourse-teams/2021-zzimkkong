package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.exception.AuthorizationHeaderUninvolvedException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private static final int TOKEN_INDEX = 1;
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String AUTHENTICATION_TYPE = "Bearer";

    private final JwtUtils jwtUtils;

    public LoginInterceptor(final JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isPreflight(request)) {
            return true;
        }

        String token = extractAccessToken(request);
        jwtUtils.validateToken(token);
        return true;
    }

    private boolean isPreflight(HttpServletRequest request) {
        return request.getMethod().equals(HttpMethod.OPTIONS.toString());
    }

    private String extractAccessToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION_HEADER_KEY);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.toLowerCase().startsWith(AUTHENTICATION_TYPE.toLowerCase())) {
                return value.split(" ")[TOKEN_INDEX];
            }
        }
        throw new AuthorizationHeaderUninvolvedException();
    }
}

