package com.woowacourse.zzimkkong.config.logaspect;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static java.lang.String.join;
import static java.util.stream.Collectors.toMap;

@Component
@Order(0)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
    private final Set<String> EXCEPT_URIS = new HashSet<>() {
        {
            /**
             * request 로깅 제외할 URI 추가
             * e.g. add("/a/uri/path");
             * */
        }
    };

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestToUse =
                request instanceof ContentCachingRequestWrapper ?
                        (ContentCachingRequestWrapper) request : new ContentCachingRequestWrapper(request);

        ContentCachingResponseWrapper responseToUse =
                response instanceof ContentCachingResponseWrapper ?
                        (ContentCachingResponseWrapper) response : new ContentCachingResponseWrapper(response);

        long startTimeMillis = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            if (!isAsyncStarted(requestToUse)) {
                afterRequest(requestToUse, responseToUse, System.currentTimeMillis() - startTimeMillis);
            }
        }
    }

    private void afterRequest(final ContentCachingRequestWrapper request,
                              final ContentCachingResponseWrapper response,
                              long processingTimeMillis) throws IOException {
        try {
            if (isLoggingRequestRequired(request)) {
                StringBuilder logMsgBuilder = new StringBuilder()
                        .append("[").append(processingTimeMillis).append("ms] ")
                        .append("[").append(response.getStatus()).append("]");

                appendIfNotBlank(request.getMethod(), (method) -> logMsgBuilder.append("[HTTP METHOD: ").append(method).append("] "));

                appendIfNotBlank(request.getRequestURI(), (requUri) -> logMsgBuilder.append("[PATH INFO: ").append(requUri).append("] "));

                appendIfNotBlank(extractHeaders(request),
                        (headerNameValuesPair) -> logMsgBuilder.append("[REQUEST HEADER: ").append(headerNameValuesPair).append("] "));

                appendIfNotEmpty(request.getParameterMap()
                                .entrySet()
                                .stream()
                                .collect(toMap(Map.Entry::getKey, entry -> join(",", entry.getValue()), (dupA, dupB) -> dupA, LinkedHashMap::new)),
                        (reqParam) -> logMsgBuilder.append("[REQUEST PARAMETERS: ").append(reqParam).append("] "));

                appendIfNotBlank(new String(request.getContentAsByteArray(), request.getCharacterEncoding()),
                        (reqBody) -> logMsgBuilder.append("[REQUEST BODY: ").append(reqBody).append("] "));

                appendIfNotBlank(request.getRemoteAddr(),
                        (remoteAddr) -> logMsgBuilder.append("[REMOTE ADDRESS: ").append(request.getRemoteAddr()).append("] "));

                appendIfNotBlank(new String(response.getContentAsByteArray(), response.getCharacterEncoding()),
                        (resBody) -> {
                            if (resBody.getBytes().length > 1024) {
                                resBody = new String(resBody.getBytes(), 0, 1024);
                            }
                            logMsgBuilder.append("[RESPONSE: ").append(resBody).append("]");
                        });

                if (HttpStatus.valueOf(response.getStatus()).is5xxServerError()) {
                    log.error(logMsgBuilder.toString());
                } else {
                    log.info(logMsgBuilder.toString());
                }
            }
        } finally {
            response.copyBodyToResponse();
            removeMDCInfo();
        }
    }

    private boolean isLoggingRequestRequired(final ContentCachingRequestWrapper request) {
        return doesNotContainExceptUri(request) && log.isInfoEnabled();
    }

    private boolean doesNotContainExceptUri(final ContentCachingRequestWrapper request) {
        for (String uri : EXCEPT_URIS) {
            if (request.getRequestURI().contains(uri)) {
                return false;
            }
        }
        return true;
    }

    private void appendIfNotBlank(final String value, final Consumer<String> appender) {
        if (StringUtils.isNotBlank(value)) {
            appender.accept(value);
        }
    }

    private void appendIfNotEmpty(final Map<?, ?> value, final Consumer<Map<?, ?>> appender) {
        if (!CollectionUtils.isEmpty(value)) {
            appender.accept(value);
        }
    }

    private void removeMDCInfo() {
        MDC.remove("traceId");
    }

    private String extractHeaders(final HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames(), headerValues;

        List<String> headerNameValuesPairList = new ArrayList<>();

        String headerName;
        StringBuilder headerValueBuilder = new StringBuilder();

        while (headerNames.hasMoreElements()) {

            headerName = headerNames.nextElement();
            headerValueBuilder.setLength(0);

            headerValues = request.getHeaders(headerName);

            while (headerValues.hasMoreElements()) {
                headerValueBuilder.append(headerValues.nextElement()).append("|");
            }

            if (headerValueBuilder.length() > 0) {
                headerValueBuilder.setLength(headerValueBuilder.length() - 1);
                headerNameValuesPairList.add(headerName + ":" + headerValueBuilder.toString());
            }
        }

        return headerNameValuesPairList.isEmpty() ? null : String.join(", ", headerNameValuesPairList);
    }
}
