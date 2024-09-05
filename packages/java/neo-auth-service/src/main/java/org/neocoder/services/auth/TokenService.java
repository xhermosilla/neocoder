package org.neocoder.services.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.neocoder.services.auth.exception.InvalidTokenException;

public class TokenService {
    private String secret;
    private String issuer;
    private int expTime;
    private static final String USERNAME = "";
    private static final String ROLES = "";

    /**
     * Constructs a new {@code TokenService} with the given secret, issuer, and
     * expiration time.
     *
     * @param secret  the secret key used for signing the token.
     * @param issuer  the issuer of the token.
     * @param expTime the expiration time of the token in seconds.
     */
    public TokenService(String secret, String issuer, int expTime) {
        this.secret = secret;
        this.issuer = issuer;
        this.expTime = expTime;
    }

    /**
     * Generates a JWT token for the specified user and roles.
     *
     * @param user  the username for whom the token is being generated.
     * @param roles the roles assigned to the user.
     * @return the generated JWT token as a {@code String}.
     */
    private String generate(String user, List<String> roles) {
        Instant now = Instant.now();
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(expTime)))
                .withClaim(USERNAME, user)
                .withClaim(ROLES, roles)
                .sign(algorithm);
    }

    /**
     * Decodes a JWT token into a {@code Claims} object.
     *
     * @param token the JWT token to decode.
     * @return the decoded {@code Claims} object.
     * @throws InvalidTokenException if the token is invalid or cannot be decoded.
     */
    public Claims decode(String token) throws InvalidTokenException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);

            long exp = decodedJWT.getExpiresAt().getTime() / 1000; // Convert milliseconds to seconds
            long iat = decodedJWT.getIssuedAt().getTime() / 1000;
            String iss = decodedJWT.getIssuer();
            String username = decodedJWT.getClaim(USERNAME).asString();
            List<String> roles = decodedJWT.getClaim(ROLES).asList(String.class);

            return new Claims(exp, iat, iss, username, roles);

        } catch (Exception e) {
            throw new InvalidTokenException("Invalid JWT token", e);
        }
    }

    /**
     * Verifies the validity of a JWT token and returns its claims.
     *
     * @param token the JWT token to verify.
     * @return the {@code Claims} object extracted from the verified token.
     * @throws InvalidTokenException if the token is invalid, expired, or has an
     *                               incorrect issuer.
     */
    private Claims verify(String token) throws InvalidTokenException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);

            long exp = decodedJWT.getExpiresAt().getTime() / 1000; // Convert milliseconds to seconds
            long iat = decodedJWT.getIssuedAt().getTime() / 1000;
            String iss = decodedJWT.getIssuer();
            String username = decodedJWT.getClaim(USERNAME).asString();
            List<String> roles = decodedJWT.getClaim(ROLES).asList(String.class);

            Claims claims = new Claims(exp, iat, iss, username, roles);

            if (claims.isExpired()) {
                throw new InvalidTokenException("Token has expired");
            }

            if (!claims.isIssuedBy(iss)) {
                throw new InvalidTokenException("Invalid issuer");
            }

            return claims;
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid token", e);
        }
    }

    /**
     * Refreshes the JWT token by generating a new one with the same user and roles.
     *
     * @param token the expired or soon-to-be-expired JWT token to refresh.
     * @return a new JWT token as a {@code String}.
     */
    public String refresh(String token) {
        Claims payload = verify(token);
        return generate(payload.username, payload.roles);
    }
}
