package com.eventzone.eventzone_backend.controller;

import com.eventzone.eventzone_backend.dto.UpdateRoleRequest;
import com.eventzone.eventzone_backend.dto.UserResponse;
import com.eventzone.eventzone_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateRoleRequest request) {
        UserResponse response = userService.updateUserRole(userId, request);
        return ResponseEntity.ok(response);
    }
    
}
