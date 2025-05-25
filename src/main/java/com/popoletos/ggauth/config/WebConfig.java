package com.popoletos.ggauth.config;

import com.popoletos.ggauth.interceptor.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RequestLoggingInterceptor requestLoggingInterceptor;
    private final RequestIdentifierInterceptor requestIdentifierInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;
    private final ApplicationTokenInterceptor applicationTokenInterceptor;
    private final ApplicationIdInterceptor applicationIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor);
        registry.addInterceptor(requestIdentifierInterceptor);
        registry.addInterceptor(rateLimitInterceptor);
        registry.addInterceptor(applicationIdInterceptor);
        registry.addInterceptor(applicationTokenInterceptor);
    }
}
