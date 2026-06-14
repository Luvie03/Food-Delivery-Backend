package com.fooddelivery.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // List of endpoints that DO NOT require a token
    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/register",
            "/api/auth/login"
    );

    // Checks if the request path is in the open endpoints list
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}