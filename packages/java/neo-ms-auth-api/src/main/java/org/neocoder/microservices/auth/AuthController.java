package org.neocoder.microservices.auth;

import org.neocoder.microservices.auth.model.*;
import org.neocoder.microservices.auth.neo_auth_service.Claims;
import org.neocoder.microservices.auth.neo_auth_service.TokenService;
import org.neocoder.microservices.auth.neo_auth_service.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;
    private static final String PASS = "admin";

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    // POST: /auth/login
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        if (!PASS.equals(username) || !PASS.equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Generate the token
        String token = tokenService.generate(username, Collections.singletonList(PASS));

        // Decode the token to get the expiration time
        Claims claims = tokenService.decode(token);

        return ResponseEntity.ok(new TokenResponse(
                claims.getExp(), // Expiration obtained from the generated token
                token,
                "Bearer"));
    }

    // POST: /auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);

        try {
            String refreshedToken = tokenService.refresh(token);

            // Decode the new token to get the expiration time
            Claims claims = tokenService.decode(refreshedToken);

            return ResponseEntity.ok(new TokenResponse(
                    claims.getExp(),
                    refreshedToken,
                    "Bearer"));
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException("Unauthorized: Invalid token");
        }
    }

    // POST: /auth/validate
    @PostMapping("/validate")
    public ResponseEntity<ValidateTokenResponse> validate(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);

        try {
            Claims claims = tokenService.verify(token);

            // Format the expiration date
            OffsetDateTime expiration = Instant.ofEpochSecond(claims.getExp()).atOffset(ZoneOffset.ofHours(2));

            return ResponseEntity.ok(new ValidateTokenResponse(
                    claims.getExp(),
                    expiration.toString(),
                    true));
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException("Unauthorized: Invalid token");
        }
    }

    // Helper method to extract the token from the "Authorization" header.
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new InvalidTokenException("Invalid token");
    }
}
