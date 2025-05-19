package com.popoletos.ggauth.controller.token;

import com.popoletos.ggauth.exceptions.InvalidTokenException;
import com.popoletos.ggauth.model.token.ApplicationTokenSetRequest;
import com.popoletos.ggauth.model.token.TokenSetResponse;
import com.popoletos.ggauth.model.token.UserTokenSetRequest;
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
    public TokenSetResponse generateUserTokenSet(@RequestBody UserTokenSetRequest request) {
        var tokenSet = tokenService.generateUserTokenSet(request.playerId());

        return TokenSetResponse.builder()
                .accessToken(tokenSet.accessToken())
                .refreshToken(tokenSet.refreshToken())
                .build();
    }

    @PostMapping("/generate/application")
    @Counted("controller.token.generate.application.count")
    public TokenSetResponse generateApplicationTokenSet(@RequestBody ApplicationTokenSetRequest request) {
        var tokenSet = tokenService.generateAppTokenSet(request.applicationId());

        return TokenSetResponse.builder()
                .accessToken(tokenSet.accessToken())
                .refreshToken(tokenSet.refreshToken())
                .build();
    }

    @PostMapping("/validate")
    @Counted("controller.token.validate.count")
    public void validateToken(@RequestHeader("Authorization") String authHeader) {
        var token = authHeader.replace("Bearer ", "");

        if (!tokenService.validateToken(token)) {
            throw new InvalidTokenException("Invalid token");
        }
    }
}
