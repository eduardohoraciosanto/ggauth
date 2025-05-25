package com.popoletos.ggauth.interceptor;

import com.popoletos.ggauth.context.RequestAttributeNames;
import com.popoletos.ggauth.ratelimit.RateLimit;
import com.popoletos.ggauth.ratelimit.RateLimitService;
import com.popoletos.ggauth.ratelimit.RateLimitTiers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Component
@Slf4j
@AllArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var requesterId = getRequesterId(request);

        if (handler instanceof HandlerMethod handlerMethod) {
            var annotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RateLimit.class);
            if (annotation != null) {
                var tier = RateLimitTiers.valueOf(annotation.tier());
                log.info("Applying {} RateLimit for requesterId={}", tier, requesterId);
                if (rateLimitService.tryConsumeToken(tier, requesterId, annotation.operationCost())) {
                    log.info("known requesterId={} allowed", requesterId);
                    return true;
                }
                log.info("known requesterId={} denied", "someID");
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return false;
            }
        }
        // we'll apply the GLOBAL Rate Limit protection otherwise
        log.info("Applying Global RateLimit for requesterId={}", requesterId);
        if (rateLimitService.tryConsumeToken(RateLimitTiers.GLOBAL, requesterId, 1)) {
            log.info("requesterId={} allowed", requesterId);
            return true;
        }
        log.info("requesterId={} denied", requesterId);
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        return false;
    }

    private String getRequesterId(HttpServletRequest request) {
        return Objects.requireNonNullElse(request.getAttribute(RequestAttributeNames.REQUESTER_ID), "unknown")
                .toString();
    }
}
