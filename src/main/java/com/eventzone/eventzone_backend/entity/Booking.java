package com.eventzone.eventzone_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String bookingRef;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    @Builder.Default
    private String status = "CONFIRMED";
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime bookedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_category_id", nullable = false)
    private TicketCategory ticketCategory;
    
    @PrePersist
    protected void onCreate() {
        if (bookedAt == null) {
            bookedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "CONFIRMED";
        }
    }
}
