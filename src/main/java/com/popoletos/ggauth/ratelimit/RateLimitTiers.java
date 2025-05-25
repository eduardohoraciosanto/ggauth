package com.popoletos.ggauth.ratelimit;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@Getter
@AllArgsConstructor
public enum RateLimitTiers {
    APPLICATION(50, Duration.ofMinutes(10)),
    TOKEN(25, Duration.ofMinutes(10)),
    GLOBAL(100, Duration.ofMinutes(20));

    private final Integer tokenCount;
    private final Duration tokenRefillInterval;
}
