package com.popoletos.ggauth.controller.ping;

import com.popoletos.ggauth.model.ping.PingResponse;
import com.popoletos.ggauth.ratelimit.RateLimit;
import com.popoletos.ggauth.ratelimit.RateLimitTiers;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Timed("controller.ping")
public class PingController {
    private final String version;

    public PingController(@Value("${app.version:development}") String version) {
        this.version = version;
    }

    @GetMapping("/ping")
    @Counted("controller.ping.count")
    @RateLimit(tier = RateLimitTiers.GLOBAL)
    public PingResponse ping() {
        return PingResponse.builder().version(version).build();
    }
}
