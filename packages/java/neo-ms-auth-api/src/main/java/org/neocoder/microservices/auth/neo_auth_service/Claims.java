package org.neocoder.microservices.auth.neo_auth_service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Claims {

    /**
     * Expiration time of the token (in epoch seconds).
     */
    @JsonProperty("exp")
    private long exp;

    /**
     * The time at which the token was issued (in epoch seconds).
     */
    @JsonProperty("iat")
    private long iat;

    /**
     * The issuer of the token.
     */
    @JsonProperty("iss")
    private String iss;

    /**
     * The username associated with the token.
     */
    @JsonProperty
    private String username;

    /**
     * The roles assigned to the token.
     */
    @JsonProperty
    private List<String> roles;

    public Claims() {
    }

    public Claims(long exp, long iat, String iss, String username, List<String> roles) {
        this.exp = exp;
        this.iat = iat;
        this.iss = iss;
        this.username = username;
        this.roles = roles;
    }

    // Getters and Setters

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    // Methods

    /**
     * Checks if the token has expired.
     *
     * @return {@code true} if the token is expired, otherwise {@code false}.
     */
    public boolean isExpired() {
        return exp < Instant.now().getEpochSecond();
    }

    /**
     * Checks if the token was issued by the given issuer.
     *
     * @param issuer the issuer to check against.
     * @return {@code true} if the token was issued by the given issuer, otherwise
     *         {@code false}.
     */
    public boolean isIssuedBy(String issuer) {
        return this.iss.equals(issuer);
    }

    /**
     * Checks if the token was issued for the given username.
     *
     * @param user the username to check against.
     * @return {@code true} if the token was issued for the given user, otherwise
     *         {@code false}.
     */
    public boolean isIssuedFor(String user) {
        return this.username.equals(user);
    }

    /**
     * Checks if the token has the given role.
     *
     * @param role the role to check against.
     * @return {@code true} if the token contains the given role, otherwise
     *         {@code false}.
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    /**
     * Deserializes a JSON string to a {@code Claims} object.
     *
     * @param json the JSON string representing a {@code Claims} object.
     * @return the {@code Claims} object deserialized from the JSON string.
     * @throws IOException if there is an error during deserialization.
     */
    public static Claims fromJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Claims.class);
    }

    /**
     * Serializes the {@code Claims} object to a JSON string.
     *
     * @return a JSON string representation of the {@code Claims} object.
     * @throws IOException if there is an error during serialization.
     */
    public String toJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
