package org.neocoder.neo_ms_auth_api;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neocoder.microservices.auth.NeoAuthApplication;
import org.neocoder.microservices.auth.model.LoginRequest;
import org.neocoder.microservices.auth.model.TokenResponse;
import org.neocoder.microservices.auth.model.ValidateTokenResponse;
import org.neocoder.services.auth.Claims;
import org.neocoder.services.auth.TokenService;
import org.neocoder.services.auth.exception.InvalidTokenException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.neocoder.microservices.auth.AuthController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

@TestPropertySource(properties = {
        "auth.token.expiration=3600",
        "auth.login.defaultUser=testUser",
        "auth.login.defaultPassword=testPassword"
})
@SpringBootTest(classes = NeoAuthApplication.class)

class AuthControllerTest {

    @MockBean
    private TokenService tokenService;

    @Autowired
    private AuthController authController;

    @Test
    void testLoginSuccess() {

        LoginRequest request = new LoginRequest("testUser", "testPassword");
        String generatedToken = "mockedToken";
        when(tokenService.generate(eq("testUser"), eq(List.of("admin")))).thenReturn(generatedToken);

        ResponseEntity<TokenResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(generatedToken, response.getBody().token());
        assertEquals("Bearer", response.getBody().tokenType());
    }

    @Test
    void testLoginUnauthorized() {
        LoginRequest request = new LoginRequest("wrongUser", "wrongPassword");

        ResponseEntity<TokenResponse> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testRefreshSuccess() throws InvalidTokenException {
        String oldToken = "oldToken";
        String newToken = "newToken";
        when(tokenService.refresh(oldToken)).thenReturn(newToken);

        ResponseEntity<TokenResponse> response = authController.refresh("Bearer " + oldToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newToken, response.getBody().token());
    }

    @Test
    void testRefreshInvalidToken() throws InvalidTokenException {
        String invalidToken = "invalidToken";
        when(tokenService.refresh(invalidToken)).thenThrow(new InvalidTokenException("Invalid token"));

        ResponseEntity<TokenResponse> response = authController.refresh("Bearer " + invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testValidateSuccess() throws InvalidTokenException {
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
    void testValidateInvalidToken() throws InvalidTokenException {
        String invalidToken = "invalidToken";
        when(tokenService.verify(invalidToken)).thenThrow(new InvalidTokenException("Invalid token"));

        ResponseEntity<ValidateTokenResponse> response = authController.validate("Bearer " + invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());

        assertFalse(response.getBody().valid());
    }
}
