package com.eventzone.eventzone_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoleRequest {
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ATTENDEE|ORGANISER|ADMIN", message = "Role must be ATTENDEE, ORGANISER, or ADMIN")
    private String role;
    
}
