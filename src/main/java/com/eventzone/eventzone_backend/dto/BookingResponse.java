package com.eventzone.eventzone_backend.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    
    private UUID bookingId;
    private String bookingRef;
    private String eventTitle;
    private String ticketCategoryName;
    private Integer quantity;
    private String status;
    
}
