package com.eventzone.eventzone_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;
    
    @NotBlank(message = "Venue is required")
    private String venue;
    
    private String coverImageUrl;
    
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
    
    @NotNull(message = "Organiser ID is required")
    private UUID organiserId;
    
}
