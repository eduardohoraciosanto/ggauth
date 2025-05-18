package com.popoletos.ggauth.controller.token;

import com.popoletos.ggauth.exceptions.InvalidTokenException;
import com.popoletos.ggauth.model.token.TokenSetRequest;
import com.popoletos.ggauth.model.token.TokenSetResponse;
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

    @PostMapping("/generate")
    @Counted("controller.token.generate.count")
    public TokenSetResponse generateTokenSet(@RequestBody TokenSetRequest request) {
        var tokenSet = tokenService.generateTokenSet(request.playerId());

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
