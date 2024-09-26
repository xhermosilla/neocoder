package org.neocoder.microservices.auth;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.neocoder.microservices.auth.model.*;
import org.neocoder.services.auth.Claims;
import org.neocoder.services.auth.TokenService;
import org.neocoder.services.auth.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;

    @Value("${auth.token.expiration}")
    private int expiration;

    @Value("${auth.login.defaultUser}")
    private String defaultUser;

    @Value("${auth.login.defaultPassword}")
    private String defaultPassword;

    /**
     * Constructor.
     *
     * @param tokenService TokenService object
     */
    @Autowired
    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Login endpoint.
     *
     * @param credentials LoginRequest object
     * @return TokenResponse object
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest credentials) {
        String username = credentials.username();
        String password = credentials.password();

        if (!defaultUser.equals(username) || !defaultPassword.equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String token = tokenService.generate(username, List.of("admin"));
        return ResponseEntity.ok(new TokenResponse(expiration, token, "Bearer"));
    }

    /**
     * Refresh endpoint.
     *
     * @param authHeader Authorization header
     * @return TokenResponse object
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(new TokenResponse(3800, "token", "Bearer"));
    }

    /**
     * Validate endpoint.
     *
     * @param authHeader Authorization header
     * @return ValidateTokenResponse object
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidateTokenResponse> validate(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");

        try {
            Claims claims = tokenService.verify(token);

            Instant expirationInstant = Instant.ofEpochSecond(claims.getExp());
            String formattedExpTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.of("UTC+2"))
                    .format(expirationInstant);

            return ResponseEntity.ok(new ValidateTokenResponse(claims.getExp(), formattedExpTime, true));

        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ValidateTokenResponse(0, null, false));
        }
    }

}
