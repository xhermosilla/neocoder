import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.neocoder.services.auth.Claims;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class ClaimsTest {

    @Test
    void testIsExpired() {
        Claims claims = new Claims(
                Instant.now().getEpochSecond() - 10, // Expired 10 seconds ago
                Instant.now().getEpochSecond(),
                "issuer",
                "username",
                Arrays.asList("role1", "role2"));

        assertTrue(claims.isExpired(), "The token should be expired");
    }

    @Test
    void testIsNotExpired() {
        Claims claims = new Claims(
                Instant.now().getEpochSecond() + 3600, // Expires in 1 hour
                Instant.now().getEpochSecond(),
                "issuer",
                "username",
                Arrays.asList("role1", "role2"));

        assertFalse(claims.isExpired(), "The token should not be expired");
    }

    @Test
    void testIsIssuedBy() {
        Claims claims = new Claims(
                Instant.now().getEpochSecond() + 3600,
                Instant.now().getEpochSecond(),
                "issuer",
                "username",
                Arrays.asList("role1", "role2"));

        assertTrue(claims.isIssuedBy("issuer"), "The token should be issued by the correct issuer");
    }

    @Test
    void testIsNotIssuedBy() {
        Claims claims = new Claims(
                Instant.now().getEpochSecond() + 3600,
                Instant.now().getEpochSecond(),
                "issuer",
                "username",
                Arrays.asList("role1", "role2"));

        assertFalse(claims.isIssuedBy("differentIssuer"), "The token should not be issued by a different issuer");
    }

    @Test
    void testIsIssuedFor() {
        Claims claims = new Claims(
                Instant.now().getEpochSecond() + 3600,
                Instant.now().getEpochSecond(),
                "issuer",
                "username",
                Arrays.asList("role1", "role2"));

        assertTrue(claims.isIssuedFor("username"), "The token should be issued for the correct user");
    }

    @Test
    void testIsNotIssuedFor() {
        Claims claims = new Claims(
                Instant.now().getEpochSecond() + 3600,
                Instant.now().getEpochSecond(),
                "issuer",
                "username",
                Arrays.asList("role1", "role2"));

        assertFalse(claims.isIssuedFor("differentUser"), "The token should not be issued for a different user");
    }

    @Test
    void testHasRole() {
        Claims claims = new Claims(
                Instant.now().getEpochSecond() + 3600,
                Instant.now().getEpochSecond(),
                "issuer",
                "username",
                Arrays.asList("role1", "role2", "role3"));

        assertTrue(claims.hasRole("role1"), "The token should have the specified role");
    }

    @Test
    void testDoesNotHaveRole() {
        Claims claims = new Claims(
                Instant.now().getEpochSecond() + 3600,
                Instant.now().getEpochSecond(),
                "issuer",
                "username",
                Arrays.asList("role1", "role2"));

        assertFalse(claims.hasRole("role3"), "The token should not have the unspecified role");
    }

}
