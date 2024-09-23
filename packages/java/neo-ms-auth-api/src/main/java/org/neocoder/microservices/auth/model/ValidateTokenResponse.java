package org.neocoder.microservices.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidateTokenResponse {

    @JsonProperty("expires")
    private long expires;

    @JsonProperty("expires_formatted")
    private String expiresFormatted;

    @JsonProperty("valid")
    private boolean valid;

    public ValidateTokenResponse() {
    }

    public ValidateTokenResponse(long expires, String expiresFormatted, boolean valid) {
        this.expires = expires;
        this.expiresFormatted = expiresFormatted;
        this.valid = valid;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public String getExpiresFormatted() {
        return expiresFormatted;
    }

    public void setExpiresFormatted(String expiresFormatted) {
        this.expiresFormatted = expiresFormatted;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
