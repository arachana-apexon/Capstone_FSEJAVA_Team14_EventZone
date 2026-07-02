package com.eventzone.eventzone_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    private String message;
    private String email;
    private String role;
    
}
