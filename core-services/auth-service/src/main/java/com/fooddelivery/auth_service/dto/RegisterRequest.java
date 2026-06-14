package com.fooddelivery.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @JsonProperty("fullName")
        @NotBlank(message = "Full name is required")
        String fullName,

        @JsonProperty("email")
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @JsonProperty("password")
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @JsonProperty("role")
        @NotBlank(message = "Role is required")
        String role
) {}