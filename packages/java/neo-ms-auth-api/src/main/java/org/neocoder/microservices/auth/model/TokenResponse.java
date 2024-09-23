package org.neocoder.microservices.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse {

    @JsonProperty("expires")
    private long expires;

    @JsonProperty("token")
    private String token;

    @JsonProperty("token_type")
    private String tokenType;

    public TokenResponse() {
    }

    public TokenResponse(long expires, String token, String tokenType) {
        this.expires = expires;
        this.token = token;
        this.tokenType = tokenType;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

}
