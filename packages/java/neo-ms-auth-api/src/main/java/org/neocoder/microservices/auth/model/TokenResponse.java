package org.neocoder.microservices.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Token response object.
 *
 * @param expires   Token expiration time
 * @param token     Token
 * @param tokenType Token type
 */
public record TokenResponse(
        @JsonProperty("expires") long expires,
        @JsonProperty("token") String token,
        @JsonProperty("token_type") String tokenType) {
}
