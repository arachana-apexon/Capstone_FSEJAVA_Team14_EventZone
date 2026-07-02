package com.eventzone.eventzone_backend.service;

import com.eventzone.eventzone_backend.dto.UpdateRoleRequest;
import com.eventzone.eventzone_backend.dto.UserResponse;
import com.eventzone.eventzone_backend.entity.User;
import com.eventzone.eventzone_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public UserResponse updateUserRole(UUID userId, UpdateRoleRequest request) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        Objects.requireNonNull(request, "Update role request cannot be null");
        
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Update role
        String newRole = Objects.requireNonNull(request.getRole(), "Role cannot be null");
        user.setRole(newRole);
        
        // Save user
        User updatedUser = userRepository.save(user);
        
        // Return response
        return UserResponse.builder()
                .id(updatedUser.getId())
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole())
                .build();
    }
    
}
