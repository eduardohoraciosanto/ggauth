package com.popoletos.ggauth.service;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {
    private TokenService tokenService;
    private static final Integer accessTokenDurationMins = 2;
    private static final Integer refreshTokenDurationMins = 10;
    private static final String SECRET_KEY = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6";
    private static final SecretKey testKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private static final String TEST_ISSUER = "testIssuer";
    private static final JwtParser tokenParser =
            Jwts.parser().verifyWith(testKey).build();

    @BeforeEach
    void beforeEach() {
        tokenService = new TokenService(
                Jwts.builder(), SECRET_KEY, TEST_ISSUER, accessTokenDurationMins, refreshTokenDurationMins);
    }

    @Test
    void generateTokenSet() {
        var playerId = "somePlayerId";
        var startingInstant = Instant.now();

        // execute
        var tokenSet = tokenService.generateTokenSet(playerId);

        // configure and assert
        var parsedAccessToken = tokenParser.parseSignedClaims(tokenSet.accessToken());
        var parsedRefreshToken = tokenParser.parseSignedClaims(tokenSet.refreshToken());

        assertEquals(playerId, parsedAccessToken.getPayload().getSubject());
        assertEquals(TEST_ISSUER, parsedAccessToken.getPayload().getIssuer());
        assertTrue(startingInstant
                .plus(Duration.ofMinutes(accessTokenDurationMins + 1))
                .isAfter(parsedAccessToken.getPayload().getExpiration().toInstant()));

        assertEquals(playerId, parsedRefreshToken.getPayload().getSubject());
        assertEquals(TEST_ISSUER, parsedRefreshToken.getPayload().getIssuer());
        assertTrue(startingInstant
                .plus(Duration.ofMinutes(refreshTokenDurationMins + 1))
                .isAfter(parsedRefreshToken.getPayload().getExpiration().toInstant()));
    }

    @Test
    void validateToken_validJWT() {
        var tokenSet = tokenService.generateTokenSet(TEST_ISSUER);

        assertTrue(tokenService.validateToken(tokenSet.accessToken()));
    }

    @Test
    void validateToken_invalidIssuer() {
        var otherTokenService = new TokenService(
                Jwts.builder(), SECRET_KEY, "otherIssuer", accessTokenDurationMins, refreshTokenDurationMins);
        var tokenSet = otherTokenService.generateTokenSet(TEST_ISSUER);

        assertFalse(tokenService.validateToken(tokenSet.accessToken()));
    }

    @Test
    void validateToken_invalidJWT() {
        var testToken = "eyJzdWIiOiJzb21lUGxheWV"
                + "ySWQiLCJpc3MiOiJvdGhlcklzc3VlciIsImV4cCI6MTc0NzQ3MDk5MX0.eAg44stj7SHB9W3p0kaFGh9SkaxjjP4pA4qZHstUZSI";

        assertFalse(tokenService.validateToken(testToken));
    }
}