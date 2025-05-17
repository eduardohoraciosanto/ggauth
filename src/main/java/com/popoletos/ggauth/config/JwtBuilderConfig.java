package com.popoletos.ggauth.config;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtBuilderConfig {
    @Bean
    public JwtBuilder jwtBuilder() {
        return Jwts.builder();
    }
}
