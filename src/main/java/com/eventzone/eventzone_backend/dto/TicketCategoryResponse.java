package com.eventzone.eventzone_backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCategoryResponse {
    
    private UUID id;
    private String name;
    private BigDecimal price;
    private Integer totalSeats;
    private Integer availableSeats;
    private String eventTitle;
}
