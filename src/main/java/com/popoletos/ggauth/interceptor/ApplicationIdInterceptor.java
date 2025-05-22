package com.popoletos.ggauth.interceptor;

import annotations.RequiresApplicationId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class ApplicationIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            var annotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequiresApplicationId.class);
            if (annotation != null) {
                log.info("Application id verification running.");
                var applicationId = request.getHeader("Application-id");
                if (StringUtils.hasText(applicationId)) {
                    // TODO: Add proper DB based APP ID verification
                    log.info("Application id is {}", applicationId);
                    return true;
                }
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }
        return true;
    }
}
