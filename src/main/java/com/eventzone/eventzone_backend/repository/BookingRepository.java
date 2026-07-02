package com.eventzone.eventzone_backend.repository;

import com.eventzone.eventzone_backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    
    List<Booking> findByUserId(UUID userId);
    
    long countByTicketCategoryId(UUID ticketCategoryId);
    
}
