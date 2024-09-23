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

        // Generar el token
        String token = tokenService.generate(username, Collections.singletonList(PASS));

        // Decodificar el token para obtener el tiempo de expiración
        Claims claims = tokenService.decode(token);

        return ResponseEntity.ok(new TokenResponse(
                claims.getExp(), // Expiración obtenida del token generado
                token,
                "Bearer"));
    }

    // POST: /auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);

        try {
            String refreshedToken = tokenService.refresh(token);

            // Decodificar el nuevo token para obtener el tiempo de expiración
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

            // Formatear la fecha de expiración (UTC+2)
            OffsetDateTime expiration = Instant.ofEpochSecond(claims.getExp()).atOffset(ZoneOffset.ofHours(2));

            return ResponseEntity.ok(new ValidateTokenResponse(
                    claims.getExp(),
                    expiration.toString(),
                    true));
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException("Unauthorized: Invalid token");
        }
    }

    // Método auxiliar para extraer el token del header "Authorization".
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new InvalidTokenException("Invalid token");
    }
}