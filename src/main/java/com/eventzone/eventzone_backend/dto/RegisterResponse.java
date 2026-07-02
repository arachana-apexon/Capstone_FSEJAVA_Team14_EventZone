package com.eventzone.eventzone_backend.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    
    private UUID id;
    private String name;
    private String email;
    private String role;
    
}
