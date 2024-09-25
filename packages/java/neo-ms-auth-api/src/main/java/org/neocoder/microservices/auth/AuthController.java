package org.neocoder.microservices.auth;

import org.neocoder.microservices.auth.model.*;
import org.neocoder.services.auth.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASSWORD = "admin";
    private static final int DEFAULT_EXPTIME = 3600;
    TokenService tkn = new TokenService(DEFAULT_PASSWORD, DEFAULT_USER, DEFAULT_EXPTIME);

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

        if (!DEFAULT_USER.equals(username) || !DEFAULT_PASSWORD.equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String token = tkn.generate(username, null);

        return ResponseEntity.ok(new TokenResponse(DEFAULT_EXPTIME, token, "Bearer"));
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
        return ResponseEntity.ok(new ValidateTokenResponse(3800, "3800", true));
    }

}
