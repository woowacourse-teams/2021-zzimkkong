package com.woowacourse.s3proxy.infrastructure;

import com.woowacourse.s3proxy.exception.AuthorizationHeaderUninvolvedException;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AuthInterceptor implements HandlerInterceptor {
    private final String secretKey;

    public AuthInterceptor(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isPreflight(request)) {
            return true;
        }

        String secretKey = AuthorizationExtractor.extractAccessToken(request);
        if (secretKey.equals(this.secretKey)) {
            return true;
        }

        throw new AuthorizationHeaderUninvolvedException();
    }

    private boolean isPreflight(HttpServletRequest request) {
        return request.getMethod().equals(HttpMethod.OPTIONS.toString());
    }
}
