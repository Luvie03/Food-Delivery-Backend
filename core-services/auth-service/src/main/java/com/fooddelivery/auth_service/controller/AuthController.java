package com.fooddelivery.auth_service.controller;

import com.fooddelivery.auth_service.dto.LoginRequest;
import com.fooddelivery.auth_service.dto.RegisterRequest;
import com.fooddelivery.auth_service.model.Role;
import com.fooddelivery.auth_service.model.User;
import com.fooddelivery.auth_service.repository.UserRepository;
import com.fooddelivery.auth_service.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest req){
        if(userRepository.existsByEmail(req.email())){
           return ResponseEntity.badRequest().body("Email is already in use");
        }
        User user = User.builder()
                .fullName(req.fullName())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(Role.valueOf(req.role().toUpperCase()))
                .build();
        userRepository.save(user);
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(Map.of(
                "message","User Registered Succesfully",
                "token",token
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest req){
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Error: Invalid email or password."));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Error: Invalid email or password."));
        }

        String token = jwtUtil.generateToken(user);

        return ResponseEntity.ok(Map.of(
                "message", "Login successful!",
                "token", token,
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));

    }
}
