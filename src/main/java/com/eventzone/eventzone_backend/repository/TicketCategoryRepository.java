package com.eventzone.eventzone_backend.repository;

import com.eventzone.eventzone_backend.entity.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketCategoryRepository extends JpaRepository<TicketCategory, UUID> {
    
    List<TicketCategory> findByEventId(UUID eventId);
}
