package com.woowacourse.s3proxy.infrastructure;

import com.woowacourse.s3proxy.exception.AuthorizationHeaderUninvolvedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class AuthorizationExtractor {
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";

    private AuthorizationExtractor() {
    }
    public static String extractAccessToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION_HEADER_KEY);
        if (headers.hasMoreElements()) {
            return headers.nextElement();
        }
        throw new AuthorizationHeaderUninvolvedException();
    }
}
