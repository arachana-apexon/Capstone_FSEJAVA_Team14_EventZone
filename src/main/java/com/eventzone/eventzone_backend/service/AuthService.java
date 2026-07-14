package com.eventzone.eventzone_backend.service;

import com.eventzone.eventzone_backend.dto.LoginRequest;
import com.eventzone.eventzone_backend.dto.LoginResponse;
import com.eventzone.eventzone_backend.dto.RegisterRequest;
import com.eventzone.eventzone_backend.dto.RegisterResponse;
import com.eventzone.eventzone_backend.entity.User;
import com.eventzone.eventzone_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public RegisterResponse register(RegisterRequest request) {
        Objects.requireNonNull(request, "Register request cannot be null");
        
        String email = Objects.requireNonNull(request.getEmail(), "Email cannot be null");
        String name = Objects.requireNonNull(request.getName(), "Name cannot be null");
        String password = Objects.requireNonNull(request.getPassword(), "Password cannot be null");
        
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        
        // Create new user
        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
        
        // Save user
        @SuppressWarnings("null")
        User savedUser = Objects.requireNonNull(userRepository.save(user), "Failed to save user");
        
        // Return response
        return RegisterResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }
    
    public LoginResponse login(LoginRequest request) {
        Objects.requireNonNull(request, "Login request cannot be null");
        
        String email = Objects.requireNonNull(request.getEmail(), "Email cannot be null");
        String password = Objects.requireNonNull(request.getPassword(), "Password cannot be null");
        
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        // Verify password
        String userPassword = Objects.requireNonNull(user.getPassword(), "User password cannot be null");
        if (!passwordEncoder.matches(password, userPassword)) {
            throw new RuntimeException("Invalid email or password");
        }
        
        // Return response
        return LoginResponse.builder()
                .message("Login successful")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    
}
