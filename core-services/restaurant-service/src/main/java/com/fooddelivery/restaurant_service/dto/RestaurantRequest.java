package com.fooddelivery.restaurant_service.dto;

import com.fooddelivery.restaurant_service.model.CuisineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RestaurantRequest(
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank String phoneNumber,
        @NotNull CuisineType cuisineType
) {}
