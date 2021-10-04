package com.woowacourse.zzimkkong.infrastructure.auth;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.exception.authorization.AuthorizationHeaderUninvolvedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@LogMethodExecutionTime(group = "infrastructure")
public class AuthorizationExtractor {
    private AuthorizationExtractor() {
    }

    public static final String AUTHENTICATION_TYPE = "Bearer";
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final int TOKEN_INDEX = 1;

    public static String extractAccessToken(HttpServletRequest request) {
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
