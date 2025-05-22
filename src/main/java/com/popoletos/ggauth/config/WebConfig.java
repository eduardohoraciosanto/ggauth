package com.popoletos.ggauth.config;

import com.popoletos.ggauth.interceptor.ApplicationIdInterceptor;
import com.popoletos.ggauth.interceptor.ApplicationTokenInterceptor;
import com.popoletos.ggauth.interceptor.RequestLoggingInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RequestLoggingInterceptor requestLoggingInterceptor;
    private final ApplicationTokenInterceptor applicationTokenInterceptor;
    private final ApplicationIdInterceptor applicationIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor);
        registry.addInterceptor(applicationTokenInterceptor);
        registry.addInterceptor(applicationIdInterceptor);
    }
}
