package com.popoletos.ggauth.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that the annotated method or class is subject to rate limiting.
 *
 * <ul>
 *     <li>
 *         The {@code tier} attribute determines the named rate limit tier to apply,
 *     </li>
 *     <li>
 *         The {@code operationCost} defines how many tokens are consumed per invocation.
 *     </li>
 * </ul>
 *
 * <p>
 * Can be applied to individual methods or entire classes.
 * When applied at the class level, it applies to all methods unless overridden at method level.
 * </p>
 *
 * @see RateLimitTiers Tier definitions and limits
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RateLimit {
    RateLimitTiers tier() default RateLimitTiers.GLOBAL;

    int operationCost() default 1;
}
