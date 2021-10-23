package com.woowacourse.s3proxy.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import com.woowacourse.s3proxy.exception.AuthorizationHeaderUninvolvedException;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@PropertySource("classpath:config/s3proxy.properties")
public class AuthInterceptor implements HandlerInterceptor {
    private final String secretKey;

    public AuthInterceptor(
            @Value("${s3proxy.secret-key}") String secretKey) {
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
