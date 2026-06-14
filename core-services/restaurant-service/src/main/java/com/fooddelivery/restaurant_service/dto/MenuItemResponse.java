package com.fooddelivery.restaurant_service.dto;

import java.math.BigDecimal;

public record MenuItemResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        String imageUrl,
        Boolean isAvailable
) {}
