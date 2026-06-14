package com.fooddelivery.restaurant_service.service;

import com.fooddelivery.restaurant_service.dto.*;
import com.fooddelivery.restaurant_service.model.*;
import com.fooddelivery.restaurant_service.repository.MenuItemRepository;
import com.fooddelivery.restaurant_service.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    // --- OWNER ACTIONS ---

    @Transactional
    public RestaurantResponse createRestaurant(RestaurantRequest request, String ownerEmail) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.name())
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .cuisineType(request.cuisineType()) // Maps directly from the Record!
                .ownerEmail(ownerEmail)
                .isOpen(false) // Default to closed upon creation
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByOwner(String ownerEmail) {
        return restaurantRepository.findRestaurantByOwnerEmail(ownerEmail)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public MenuItemResponse addMenuItem(Long restaurantId, MenuItemRequest request, String ownerEmail) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Security check: Ensure the requester actually owns this restaurant
        if (!restaurant.getOwnerEmail().equals(ownerEmail)) {
            throw new RuntimeException("Unauthorized: You do not own this restaurant.");
        }

        MenuItem item = MenuItem.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .imageUrl(request.imageUrl())
                .isAvailable(true)
                .restaurant(restaurant)
                .build();

        MenuItem savedItem = menuItemRepository.save(item);

        return new MenuItemResponse(
                savedItem.getId(), savedItem.getName(), savedItem.getDescription(),
                savedItem.getPrice(), savedItem.getCategory(), savedItem.getImageUrl(), savedItem.getIsAvailable()
        );
    }

    // --- CUSTOMER ACTIONS ---

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllOpenRestaurants() {
        return restaurantRepository.findRestaurantByIsOpenTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuForRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .filter(MenuItem::getIsAvailable) // Only show available items to customers
                .map(item -> new MenuItemResponse(
                        item.getId(), item.getName(), item.getDescription(),
                        item.getPrice(), item.getCategory(), item.getImageUrl(), item.getIsAvailable()
                )).toList();
    }

    // --- UTILITY METHODS ---

    private RestaurantResponse mapToResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getPhoneNumber(),
                restaurant.getCuisineType(),
                restaurant.getIsOpen(),
                restaurant.getOwnerEmail(),
                restaurant.getCreatedAt()
        );
    }
}