package com.eventzone.eventzone_backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCategoryRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Price is required")
    private BigDecimal price;
    
    @NotNull(message = "Total seats is required")
    private Integer totalSeats;
    
    @NotNull(message = "Event ID is required")
    private UUID eventId;
}
