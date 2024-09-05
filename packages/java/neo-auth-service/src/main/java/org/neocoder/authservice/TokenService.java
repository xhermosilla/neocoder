package org.neocoder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.neocoder.exception.InvalidTokenException;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class TokenService {
    public String secret;
    public String issuer;
    public int expTime;

    public TokenService(String secret, String issuer, int expTime) {
        this.secret = secret;
        this.issuer = issuer;
        this.expTime = expTime;
    }

    private String generate(String user, List<String> roles) throws Exception {
            Instant now = Instant.now();
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create()
                    .withIssuer(issuer)
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(now.plusSeconds(expTime)))
                    .withClaim("username", user)
                    .withClaim("roles", roles)
                    .sign(algorithm);

            return token;
    }

    private Claims decode(String token) throws Exception{
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);

            long exp = decodedJWT.getExpiresAt().getTime()/1000; //Convert milliseconds to seconds
            long iat = decodedJWT.getIssuedAt().getTime()/1000;
            String iss = decodedJWT.getIssuer();
            String username = decodedJWT.getClaim("username").asString();
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

            return new Claims(exp, iat, iss, username, roles);

        }catch (Exception e){
            throw new Exception("Invalid JWT token", e);
        }
    }

    private Claims verify(String token) throws Exception{
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);

            long exp = decodedJWT.getExpiresAt().getTime()/1000; //Convert milliseconds to seconds
            long iat = decodedJWT.getIssuedAt().getTime()/1000;
            String iss = decodedJWT.getIssuer();
            String username = decodedJWT.getClaim("username").asString();
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

            Claims claims = new Claims(exp, iat, iss, username, roles);

            if(claims.isExpired()){
                throw new Exception("Token has expired");
            }

            if(!claims.isIssuedBy(iss)){
                throw new Exception("Invalid issuer");
            }

            return claims;
        }catch (Exception e){
            throw new InvalidTokenException("Invalid token", e);
        }
    }

    private String refresh(String token) throws Exception{
        Claims payload = verify(token);
        return generate(payload.username, payload.roles);
    }
}
