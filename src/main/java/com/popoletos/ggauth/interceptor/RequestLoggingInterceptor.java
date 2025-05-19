package com.popoletos.ggauth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Set<String> REDACTED_HEADERS = Set.of("Authorization");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // collect request headers
        Map<String, String> headersMap = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(name -> headersMap.put(name, request.getHeader(name)));

        // Log the incoming request data
        log.info(
                "Incoming request {} {} - Headers {}",
                request.getMethod(),
                request.getRequestURI(),
                redactHeaders(headersMap));

        return true; // Return true to proceed with the request
    }

    private Map<String, String> redactHeaders(Map<String, String> headersMap) {
        REDACTED_HEADERS.forEach(header -> headersMap.replace(header.toLowerCase(Locale.ROOT), "******"));
        return headersMap;
    }
}
