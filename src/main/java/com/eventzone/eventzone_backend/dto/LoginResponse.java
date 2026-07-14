package com.eventzone.eventzone_backend.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    private String message;
    private UUID userId;
    private String email;
    private String role;
    
}
