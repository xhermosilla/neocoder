import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neocoder.services.auth.exception.InvalidTokenException;
import org.neocoder.services.auth.TokenService;
import org.neocoder.services.auth.Claims;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private static final String SECRET_KEY = "mySecretKey";
    private static final String ISSUER = "myIssuer";
    private static final int EXPIRATION_TIME = 3600;
    private static final String USERNAME = "testUser";
    private static final List<String> ROLES = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

    private TokenService tokenService;
    private String validToken;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(SECRET_KEY, ISSUER, EXPIRATION_TIME);
        validToken = tokenService.generate(USERNAME, ROLES); // Generate a valid token for testing
    }

    @Test
    void testGenerateToken() {
        String token = tokenService.generate(USERNAME, ROLES);
        assertNotNull(token, "Generated token should not be null");
    }

    @Test
    void testDecodeValidToken() {
        Claims claims = tokenService.decode(validToken);
        assertNotNull(claims, "Decoded claims should not be null");
        assertEquals(USERNAME, claims.getUsername(), "Decoded username should match");
        assertEquals(ROLES, claims.getRoles(), "Decoded roles should match");
        assertEquals(ISSUER, claims.getIss(), "Issuer should match");
    }

    @Test
    void testDecodeInvalidToken() {
        String invalidToken = "invalid.token.string";
        assertThrows(InvalidTokenException.class, () -> tokenService.decode(invalidToken),
                "Decoding an invalid token should throw InvalidTokenException");
    }

    @Test
    void testVerifyValidToken() {
        Claims claims = tokenService.verify(validToken);
        assertNotNull(claims, "Verified claims should not be null");
        assertEquals(USERNAME, claims.getUsername(), "Verified username should match");
        assertEquals(ROLES, claims.getRoles(), "Verified roles should match");
        assertEquals(ISSUER, claims.getIss(), "Issuer should match");
    }

    @Test
    void testVerifyExpiredToken() throws InterruptedException {
        TokenService shortLivedTokenService = new TokenService(SECRET_KEY, ISSUER, 1); // 1 second expiration time
        String shortLivedToken = shortLivedTokenService.generate(USERNAME, ROLES);
        Thread.sleep(2000); // Wait for the token to expire

        assertThrows(InvalidTokenException.class, () -> shortLivedTokenService.verify(shortLivedToken),
                "Verifying an expired token should throw InvalidTokenException");
    }

    @Test
    void testRefreshToken() {
        String newToken = tokenService.refresh(validToken);
        assertNotNull(newToken, "Refreshed token should not be null");

        Claims newClaims = tokenService.decode(newToken);
        assertEquals(USERNAME, newClaims.getUsername(), "Refreshed token username should match");
        assertEquals(ROLES, newClaims.getRoles(), "Refreshed token roles should match");
    }
}
