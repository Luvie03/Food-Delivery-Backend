package com.fooddelivery.auth_service.dto;

import org.hibernate.sql.ast.spi.StringBuilderSqlAppender;

public record AuthResponse(
        String token,
        String email,
        String role
) {
}
