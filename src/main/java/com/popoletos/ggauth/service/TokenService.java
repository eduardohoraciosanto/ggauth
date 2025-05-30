package com.popoletos.ggauth.service;

import com.popoletos.ggauth.annotations.ToMDC;
import com.popoletos.ggauth.exceptions.InvalidTokenException;
import com.popoletos.ggauth.mdc.MdcKeys;
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
import java.nio.charset.StandardCharsets;
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
    private final Duration userAccessTokenValidity;
    private final Duration userRefreshTokenValidity;
    private final Duration appAccessTokenValidity;
    private final Duration appRefreshTokenValidity;

    public TokenService(
            JwtBuilder jwtBuilder,
            @Value("${app.auth.key}") String signingKey,
            @Value("${app.auth.issuer}") String issuer,
            @Value("${app.auth.user-access-token-expiration-minutes}") Integer userAccessTokenDurationMins,
            @Value("${app.auth.user-refresh-token-expiration-minutes}") Integer userRefreshTokenDurationMins,
            @Value("${app.auth.app-access-token-expiration-minutes}") Integer appAccessTokenDurationMins,
            @Value("${app.auth.app-refresh-token-expiration-minutes}") Integer appRefreshTokenDurationMins) {
        this.jwtBuilder = jwtBuilder;
        this.signingKey = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser().verifyWith(this.signingKey).build();
        this.issuer = issuer;
        this.userAccessTokenValidity = Duration.ofMinutes(userAccessTokenDurationMins);
        this.userRefreshTokenValidity = Duration.ofMinutes(userRefreshTokenDurationMins);
        this.appAccessTokenValidity = Duration.ofMinutes(appAccessTokenDurationMins);
        this.appRefreshTokenValidity = Duration.ofMinutes(appRefreshTokenDurationMins);
    }

    @Override
    protected final void finalize() {
        // Do nothing, this only serves to avoid Finalizer attacks due to our constructor being able to throw
        // we cannot mark the class final since we need to mock it
    }

    /**
     * Generates a token set for the specified app ID. This method creates both an access token and a refresh token
     * with appropriate expirations based on the current system time plus the defined validity periods for both tokens.
     *
     * @param appId the unique identifier od the Application.
     * @return a {@code TokenSet} containing the generated access token and refresh token.
     */
    public TokenSet generateAppTokenSet(@ToMDC(MdcKeys.APPLICATION_ID) String appId) {
        log.info("Generating token set for app: {}", appId);

        // TODO: Check the app is a known one against a DB

        var accessTokenExpiration = Instant.now().plusSeconds(appAccessTokenValidity.toSeconds());
        var refreshTokenExpiration = Instant.now().plusSeconds(appRefreshTokenValidity.toSeconds());

        return buildTokenSet(appId, accessTokenExpiration, refreshTokenExpiration);
    }

    /**
     * Generates a token set for the specified player ID. This method creates both an access token and a refresh token
     * with appropriate expirations based on the current system time plus the defined validity periods for both tokens.
     *
     * @param playerId the unique identifier for the player for whom the token set is being generated.
     * @return a {@code TokenSet} containing the generated access token and refresh token.
     */
    public TokenSet generateUserTokenSet(@ToMDC(MdcKeys.PLAYER_ID) String playerId) {
        log.info("Generating token set for playerId: {}", playerId);

        var accessTokenExpiration = Instant.now().plusSeconds(userAccessTokenValidity.toSeconds());
        var refreshTokenExpiration = Instant.now().plusSeconds(userRefreshTokenValidity.toSeconds());

        return buildTokenSet(playerId, accessTokenExpiration, refreshTokenExpiration);
    }

    private TokenSet buildTokenSet(
            @ToMDC(MdcKeys.SUBJECT) String subject, Instant accessTokenExpiration, Instant refreshTokenExpiration) {

        var baseBuilder = jwtBuilder.subject(subject).issuer(issuer).signWith(signingKey);

        var accessToken =
                baseBuilder.expiration(Date.from(accessTokenExpiration)).compact();

        var refreshToken =
                baseBuilder.expiration(Date.from(refreshTokenExpiration)).compact();

        log.info("Token built for subject {}", subject);

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

    /** <p>Extracts the subject of the token, this applies to both User and App tokens</p>
     * <p>The token will be parsed and verified in the process</p>
     * @return The subject (who) from the token
     * */
    public String getSubject(String token) {
        try {
            return jwtParser.parseSignedClaims(token).getPayload().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage(), e);
            throw new InvalidTokenException("Invalid token when extracting subject");
        }
    }
}
