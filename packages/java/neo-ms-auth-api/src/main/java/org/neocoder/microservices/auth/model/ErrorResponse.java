package org.neocoder.microservices.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Token response object.
 *
 * @param status  Error code
 * @param error   Error name
 * @param message Message specifying the error
 */
public record ErrorResponse(
        @JsonProperty("status") int status,
        @JsonProperty("error") String error,
        @JsonProperty("message") String message) {
}
