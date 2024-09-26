package org.neocoder.microservices.auth;

import java.util.List;

import org.neocoder.microservices.auth.model.*;
import org.neocoder.services.auth.TokenService;
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
        return ResponseEntity.ok(new ValidateTokenResponse(3800, "3800", true));
    }

}
