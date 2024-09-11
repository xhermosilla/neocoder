package org.neocoder.services.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AuthServiceTest {

    @Test
    void testHashPassword() {
        // Test that the hash is not null and has the expected length
        String password = "pass123";
        String hashedPassword = AuthService.hashPassword(password);
        assertNotNull(hashedPassword, "The hash should not be null");

        // BCrypt produces hashes with a length of at least 60 characters
        assertTrue(hashedPassword.length() >= 60, "The hash should have at least 60 characters");

        // Test that the generated hash is different each time due to the random salt
        String anotherHashedPassword = AuthService.hashPassword(password);
        assertNotEquals(hashedPassword, anotherHashedPassword, "Each hash should be unique due to the random salt");
    }

    @Test
    void testComparePassword() {
        String password = "pass123";
        String hashedPassword = AuthService.hashPassword(password);
        assertTrue(AuthService.comparePassword(password, hashedPassword), "The password should match its own hash");

        String wrongPassword = "wrongpass";
        assertFalse(AuthService.comparePassword(wrongPassword, hashedPassword),
                "An incorrect password should not match the hash");
    }

}
