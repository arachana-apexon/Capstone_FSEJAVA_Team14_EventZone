package com.eventzone.eventzone_backend.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String venue;
    private String coverImageUrl;
    private String categoryName;
    private UUID organiserId;
    private String organiserName;
    private Boolean active;
    
}
