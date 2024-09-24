package org.neocoder.microservices.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ValidateTokenResponse(
        @JsonProperty("expires") long expires,
        @JsonProperty("expires_formatted") String expiresFormatted,
        @JsonProperty("valid") boolean valid) {
}
