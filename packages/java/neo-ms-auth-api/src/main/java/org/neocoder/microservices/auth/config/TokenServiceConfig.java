package org.neocoder.microservices.auth.config;

import org.neocoder.services.auth.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenServiceConfig {

    /**
     * TokenService bean.
     *
     * @param secretKey  Secret key
     * @param issuer     Issuer
     * @param expiration Expiration time
     * @return TokenService object
     */
    @Bean
    public TokenService tokenService(
            @Value("${auth.token.secretKey}") String secretKey,
            @Value("${auth.token.issuer}") String issuer,
            @Value("${auth.token.expiration}") int expiration) {
        return new TokenService(secretKey, issuer, expiration);
    }
}
