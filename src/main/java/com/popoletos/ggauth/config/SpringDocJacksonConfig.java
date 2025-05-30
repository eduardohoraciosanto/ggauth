package com.popoletos.ggauth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.core.jackson.ModelResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Springdoc OpenAPI to ensure proper
 * JSON property naming strategy (SNAKE_CASE) in generated API documentation.
 * <p>
 * This class provides a custom {@link ModelResolver} bean, which is used by Springdoc
 * to resolve Java objects into OpenAPI schemas. By configuring the injected
 * {@link ObjectMapper} with {@link PropertyNamingStrategies#SNAKE_CASE},
 * it ensures that properties like 'accessToken' in Java are documented as
 * 'access_token' in the OpenAPI specification and Swagger UI.
 * </p>
 */
@Configuration
public class SpringDocJacksonConfig {
    @Bean
    public ModelResolver modelResolver(ObjectMapper objectMapper) {
        return new ModelResolver(objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));
    }
}
