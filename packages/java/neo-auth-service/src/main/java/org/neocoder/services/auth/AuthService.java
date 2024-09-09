package org.neocoder.services.auth;

import org.mindrot.jbcrypt.BCrypt;

/**
 * AuthService is a class that provides methods for password management,
 * including creating password hashes and comparing passwords.
 */
public class AuthService {

    private AuthService() {
    }

    /**
     * Generates a secure hash for the given plain-text password.
     *
     * @param password The plain-text password to be hashed.
     * @return A secure hash of the password.
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Compares a plain-text password with a previously hashed password.
     *
     * @param password       The plain-text password to verify.
     * @param hashedpassword The hashed password to compare.
     * @return {@code true} if the password matches the hash, {@code false}
     *         otherwise.
     */
    public static boolean comparePassword(String password, String hashedpassword) {
        return BCrypt.checkpw(password, hashedpassword);
    }
}
