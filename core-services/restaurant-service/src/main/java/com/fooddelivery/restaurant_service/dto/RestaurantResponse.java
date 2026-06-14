package com.fooddelivery.restaurant_service.dto;

import com.fooddelivery.restaurant_service.model.CuisineType;

import java.time.LocalDateTime;

public record RestaurantResponse(
        Long id,
        String name,
        String address,
        String phoneNumber,
        CuisineType cuisineType,
        Boolean isOpen,
        String ownerEmail,
        LocalDateTime createdAt
) {}