package org.neocoder.microservices.auth.model;

/**
 * Login request object.
 *
 * @param username Username
 * @param password Password
 */
public record LoginRequest(String username, String password) {
}
