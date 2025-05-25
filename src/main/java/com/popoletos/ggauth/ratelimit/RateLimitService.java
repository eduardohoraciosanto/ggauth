package com.popoletos.ggauth.ratelimit;

import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>RateLimit service using Bucket4J as the underlying library</p>
 * <p>There are different {@link RateLimitTiers Tiers} each with its own properties</p>
 *
 * */
@Service
@Slf4j
public class RateLimitService {
    private final Map<String, Bucket> tokenBuckets;

    RateLimitService() {
        this.tokenBuckets = new ConcurrentHashMap<>();
    }

    /**
     * Attempts to consume a single token from the rate limiter bucket associated with the given tier and identifier.
     * <p>
     * If no bucket exists for the combination of {@code tier} and {@code id}, a new one is created using
     * the token capacity and refill interval defined by the tier.
     * </p>
     *
     * @param tier the rate limit {@link RateLimitTiers tier} configuration, defining token capacity and refill behavior
     * @param id a unique identifier (e.g., user ID, API key) to scope the rate limiter bucket
     * @param cost the amount of tokens the operation is trying to consume
     * @return {@code true} if a token was successfully consumed; {@code false} if the bucket is empty
     */
    public boolean tryConsumeToken(RateLimitTiers tier, String id, Integer cost) {
        var specificBucket = tokenBuckets.computeIfAbsent(buildTierKey(tier, id), key -> {
            log.info("Creating bucket for key {}", key);
            // key not in map, we create a new bucket for this composite key
            return Bucket.builder()
                    .addLimit(limit -> limit.capacity(tier.getTokenCount())
                            .refillGreedy(tier.getTokenCount(), tier.getTokenRefillInterval()))
                    .build();
        });

        return specificBucket.tryConsume(cost);
    }

    private String buildTierKey(RateLimitTiers tier, String id) {
        return tier.name() + ":" + id;
    }
}
