package com.popoletos.ggauth.controller.token;

import annotations.RequiresApplicationId;
import com.popoletos.ggauth.annotations.RequiresApplicationToken;
import com.popoletos.ggauth.exceptions.InvalidTokenException;
import com.popoletos.ggauth.model.token.TokenSetResponse;
import com.popoletos.ggauth.model.token.UserTokenSetRequest;
import com.popoletos.ggauth.ratelimit.RateLimit;
import com.popoletos.ggauth.service.TokenService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
@Timed("controller.token")
@AllArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/generate/player")
    @Counted("controller.token.generate.player.count")
    @RequiresApplicationToken
    @RateLimit(tier = "APPLICATION")
    public TokenSetResponse generateUserTokenSet(@RequestBody UserTokenSetRequest request) {
        var tokenSet = tokenService.generateUserTokenSet(request.playerId());

        return TokenSetResponse.builder()
                .accessToken(tokenSet.accessToken())
                .refreshToken(tokenSet.refreshToken())
                .build();
    }

    @PostMapping("/generate/application")
    @Counted("controller.token.generate.application.count")
    @RequiresApplicationId
    @RateLimit(tier = "APPLICATION")
    public TokenSetResponse generateApplicationTokenSet(@RequestHeader("Application-Id") String appId) {
        var tokenSet = tokenService.generateAppTokenSet(appId);

        return TokenSetResponse.builder()
                .accessToken(tokenSet.accessToken())
                .refreshToken(tokenSet.refreshToken())
                .build();
    }

    @PostMapping("/validate")
    @Counted("controller.token.validate.count")
    @RateLimit(tier = "TOKEN")
    public void validateToken(@RequestHeader("Authorization") String authHeader) {
        var token = authHeader.replace("Bearer ", "");

        if (!tokenService.validateToken(token)) {
            throw new InvalidTokenException("Invalid token");
        }
    }
}
