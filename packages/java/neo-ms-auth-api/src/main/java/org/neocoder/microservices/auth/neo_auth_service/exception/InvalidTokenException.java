package org.neocoder.microservices.auth.neo_auth_service.exception;

public class InvalidTokenException extends RuntimeException {
    /**
     * Constructs a new {@code InvalidTokenException} with the specified detail
     * message.
     *
     * @param message the detail message.
     */
    public InvalidTokenException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code InvalidTokenException} with the specified detail
     * message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
