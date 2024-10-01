package org.neocoder.neo_ms_auth_api;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.neocoder.microservices.auth.model.LoginRequest;
import org.neocoder.microservices.auth.model.TokenResponse;
import org.neocoder.microservices.auth.model.ValidateTokenResponse;
import org.neocoder.services.auth.Claims;
import org.neocoder.services.auth.TokenService;
import org.neocoder.services.auth.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.neocoder.microservices.auth.AuthController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

class AuthControllerTest {

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController.expiration = 3600; // Puedes ajustar este valor según tu configuración
        authController.defaultUser = "defaultUser";
        authController.defaultPassword = "defaultPassword";
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest("defaultUser", "defaultPassword");
        String generatedToken = "mockedToken";
        when(tokenService.generate(eq("defaultUser"), eq(List.of("admin")))).thenReturn(generatedToken);

        ResponseEntity<TokenResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(generatedToken, response.getBody().token());
        assertEquals("Bearer", response.getBody().tokenType());
    }

    @Test
    void testLogin_Unauthorized() {
        LoginRequest request = new LoginRequest("wrongUser", "wrongPassword");

        ResponseEntity<TokenResponse> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testRefresh_Success() throws InvalidTokenException {
        String oldToken = "oldToken";
        String newToken = "newToken";
        when(tokenService.refresh(oldToken)).thenReturn(newToken);

        ResponseEntity<TokenResponse> response = authController.refresh("Bearer " + oldToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newToken, response.getBody().token());
    }

    @Test
    void testRefresh_InvalidToken() throws InvalidTokenException {
        String invalidToken = "invalidToken";
        when(tokenService.refresh(invalidToken)).thenThrow(new InvalidTokenException("Invalid token"));

        ResponseEntity<TokenResponse> response = authController.refresh("Bearer " + invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testValidate_Success() throws InvalidTokenException {
        String token = "validToken";
        Claims claims = mock(Claims.class);
        long expirationTime = Instant.now().getEpochSecond() + 3600;
        when(claims.getExp()).thenReturn(expirationTime);
        when(tokenService.verify(token)).thenReturn(claims);

        String formattedExpirationTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("UTC+2"))
                .format(Instant.ofEpochSecond(expirationTime));

        ResponseEntity<ValidateTokenResponse> response = authController.validate("Bearer " + token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ValidateTokenResponse responseBody = response.getBody();
        assertEquals(expirationTime, responseBody.expires());
        assertEquals(formattedExpirationTime, responseBody.expiresFormatted());
        assertTrue(responseBody.valid());
    }



    @Test
    void testValidate_InvalidToken() throws InvalidTokenException {
        String invalidToken = "invalidToken";
        when(tokenService.verify(invalidToken)).thenThrow(new InvalidTokenException("Invalid token"));

        ResponseEntity<ValidateTokenResponse> response = authController.validate("Bearer " + invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());

        assertFalse(response.getBody().valid());
    }

}
