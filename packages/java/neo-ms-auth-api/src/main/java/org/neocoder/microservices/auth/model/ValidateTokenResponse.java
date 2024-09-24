package org.neocoder.microservices.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Validate token response object.
 *
 * @param expires          Token expiration time
 * @param expiresFormatted Token expiration time formatted
 * @param valid            Token validity
 */
public record ValidateTokenResponse(
        @JsonProperty("expires") long expires,
        @JsonProperty("expires_formatted") String expiresFormatted,
        @JsonProperty("valid") boolean valid) {
}
