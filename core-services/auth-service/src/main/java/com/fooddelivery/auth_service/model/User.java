package com.fooddelivery.auth_service.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

   @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt=LocalDateTime.now();
    }

}
