package com.fooddelivery.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private final RouteValidator validator;

    @Value("${jwt.secret}")
    private String SECRET ;

    public AuthenticationGatewayFilterFactory(RouteValidator validator) {
        super(Config.class);
        this.validator = validator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            // 1. Is this a public GET request for the Restaurant Service? Let it through!
            if (exchange.getRequest().getMethod() == HttpMethod.GET &&
                    exchange.getRequest().getURI().getPath().startsWith("/api/restaurants")) {
                return chain.filter(exchange);
            }

            // 2. For secured routes, check if the Authorization header is present
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                try {
                    // 3. Validate the token and extract the claims
                    Claims claims = Jwts.parser()
                            .verifyWith(getSigningKey())
                            .build()
                            .parseSignedClaims(authHeader)
                            .getPayload();

                    // Extract the email (Subject) from the token
                    String userEmail = claims.getSubject();

                    // 4. THE MAGIC: Mutate the request to add the X-User-Email header
                    exchange.getRequest().mutate()
                            .header("X-User-Email", userEmail)
                            .build();

                } catch (Exception e) {
                    System.out.println("Invalid Access: " + e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
            return chain.filter(exchange);
        });
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static class Config {
        // Empty class for Spring Cloud Gateway configuration properties
    }
}