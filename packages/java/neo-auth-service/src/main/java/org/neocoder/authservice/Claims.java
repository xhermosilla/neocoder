package org.neocoder.authservice;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Claims {
    @JsonProperty("exp")
    public long exp; //Expiration time

    @JsonProperty("iat")
    public long iat; //Issued at

    @JsonProperty("iss")
    public String iss; //Issuer

    @JsonProperty
    public String username; //Username

    @JsonProperty
    public List<String> roles; //Roles

    public Claims() {
    }

    public Claims(long exp, long iat, String iss, String username, List<String> roles) {
        this.exp = exp;
        this.iat = iat;
        this.iss = iss;
        this.username = username;
        this.roles = roles;
    }

    //Check if the token has expired
    public boolean isExpired(){
        if(exp < Instant.now().getEpochSecond()){
            return true;
        } else {
            return false;
        }
    }

    //Check if the token was issued by the given issuer
    public boolean isIssuedBy(String issuer){
        if (this.iss.equals(issuer)){
            return true;
        } else {
            return false;
        }
    }

    //Check if the token was issued for the given issuer
    public boolean isIssuedFor(String user){
        if(this.username.equals(user)){
            return true;
        } else {
            return false;
        }
    }

    //Check if the token has the given role
    public boolean hasRole(String role) {
        if (roles.contains(role)){
            return true;
        } else {
            return false;
        }
    }

    //Deserialize JSON
    public static Claims fromJson(String json) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Claims.class);
    }

    //Serialize JSON
    public String toJson() throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
