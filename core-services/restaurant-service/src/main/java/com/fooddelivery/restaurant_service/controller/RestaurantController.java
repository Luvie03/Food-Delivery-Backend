package com.fooddelivery.restaurant_service.controller;

import com.fooddelivery.restaurant_service.dto.MenuItemRequest;
import com.fooddelivery.restaurant_service.dto.MenuItemResponse;
import com.fooddelivery.restaurant_service.dto.RestaurantRequest;
import com.fooddelivery.restaurant_service.dto.RestaurantResponse;
import com.fooddelivery.restaurant_service.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // --- SECURED OWNER ENDPOINTS ---

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @Valid @RequestBody RestaurantRequest request,
            @RequestHeader("X-User-Email") String ownerEmail) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.createRestaurant(request, ownerEmail));
    }

    @GetMapping("/my-restaurants")
    public ResponseEntity<List<RestaurantResponse>> getMyRestaurants(
            @RequestHeader("X-User-Email") String ownerEmail) {

        return ResponseEntity.ok(restaurantService.getRestaurantsByOwner(ownerEmail));
    }

    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<MenuItemResponse> addMenuItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemRequest request,
            @RequestHeader("X-User-Email") String ownerEmail) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.addMenuItem(restaurantId, request, ownerEmail));
    }

    // --- PUBLIC CUSTOMER ENDPOINTS ---

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllOpenRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllOpenRestaurants());
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemResponse>> getMenu(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getMenuForRestaurant(restaurantId));
    }
}