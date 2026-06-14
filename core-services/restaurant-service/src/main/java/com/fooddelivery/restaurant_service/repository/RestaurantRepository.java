package com.fooddelivery.restaurant_service.repository;

import com.fooddelivery.restaurant_service.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {
    List<Restaurant> findRestaurantByIsOpenTrue();

    List<Restaurant> findRestaurantByOwnerEmail(String ownerEmail);
    Optional<Restaurant> findRestaurantsByIdAndOwnerEmail(Long id,String email);
}
