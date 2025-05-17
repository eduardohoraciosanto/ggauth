package com.popoletos.ggauth.service;

import com.popoletos.ggauth.model.token.TokenSet;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Service class responsible for generating and validating tokens.
 * This service uses the JJWT library to build and parse JWTs.
 * The service provides two main functionalities: generating a token set for a player and validating a given token.
 *
 */
@Service
@Slf4j
public class TokenService {
    private final JwtBuilder jwtBuilder;
    private final JwtParser jwtParser;
    private final SecretKey signingKey;
    private final String issuer;
    private final Duration accessTokenValidity;
    private final Duration refreshTokenValidity;

    public TokenService(
            JwtBuilder jwtBuilder,
            @Value("${auth.key}") String signingKey,
            @Value("${auth.issuer}") String issuer,
            @Value("${auth.access-token-expiration-minutes}") Integer accessTokenDurationMins,
            @Value("${auth.refresh-token-expiration-minutes}") Integer refreshTokenDurationMins) {
        this.jwtBuilder = jwtBuilder;
        this.signingKey = Keys.hmacShaKeyFor(signingKey.getBytes());
        this.jwtParser = Jwts.parser().verifyWith(this.signingKey).build();
        this.issuer = issuer;
        this.accessTokenValidity = Duration.ofMinutes(accessTokenDurationMins);
        this.refreshTokenValidity = Duration.ofMinutes(refreshTokenDurationMins);
    }

    /**
     * Generates a token set for the specified player ID. This method creates both an access token and a refresh token
     * with appropriate expirations based on the current system time plus the defined validity periods for both tokens.
     *
     * @param playerId the unique identifier for the player for whom the token set is being generated.
     * @return a {@code TokenSet} containing the generated access token and refresh token.
     */
    public TokenSet generateTokenSet(String playerId) {
        log.info("Generating token set for playerId: {}", playerId);

        var accessTokenExpiration = Instant.now().plusSeconds(accessTokenValidity.toSeconds());
        var refreshTokenExpiration = Instant.now().plusSeconds(refreshTokenValidity.toSeconds());

        var baseBuilder = jwtBuilder.subject(playerId).issuer(issuer).signWith(signingKey);

        var accessToken =
                baseBuilder.expiration(Date.from(accessTokenExpiration)).compact();

        var refreshToken =
                baseBuilder.expiration(Date.from(refreshTokenExpiration)).compact();

        return TokenSet.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /** <p>This will validate the provided String token</p>
     * <p>Signature and issuer will be asserted and must be correct</p>
     *
     * @param token the string token to parse and validate
     * @return true if the token is valid, false otherwise
     * */
    public boolean validateToken(String token) {
        try {
            var parsedJwt = jwtParser.parseSignedClaims(token);
            if (!issuer.equals(parsedJwt.getPayload().getIssuer())) {
                log.error(
                        "Issuer mismatch, {} != {}",
                        issuer,
                        parsedJwt.getPayload().getIssuer());
                return false;
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage(), e);
            return false;
        }
    }
}
