package org.neocoder.microservices.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("expires") long expires,
        @JsonProperty("token") String token,
        @JsonProperty("token_type") String tokenType) {
}
