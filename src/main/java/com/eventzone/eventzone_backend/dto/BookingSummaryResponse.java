package com.eventzone.eventzone_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSummaryResponse {
    
    private UUID ticketCategoryId;
    private String ticketCategoryName;
    private Integer totalSeats;
    private Integer availableSeats;
    private Integer bookedSeats;
    
}
