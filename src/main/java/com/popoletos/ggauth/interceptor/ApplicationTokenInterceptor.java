package com.popoletos.ggauth.interceptor;

import com.popoletos.ggauth.annotations.RequiresApplicationToken;
import com.popoletos.ggauth.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
@AllArgsConstructor
public class ApplicationTokenInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            var annotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequiresApplicationToken.class);
            if (annotation != null) {
                // proceed token validation
                var header = request.getHeader("Authorization");
                if (StringUtils.hasText(header)) {
                    var token = header.replace("Bearer ", "");

                    var isAuth = tokenService.validateToken(token);
                    if (isAuth) {
                        var subject = tokenService.getSubject(token);
                        log.info("Application {} authenticated", subject);
                        return true;
                    }
                }

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }
        return true;
    }
}
