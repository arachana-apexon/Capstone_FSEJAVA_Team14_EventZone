package com.eventzone.eventzone_backend.repository;

import com.eventzone.eventzone_backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// @Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    
    List<Event> findByCategoryId(UUID categoryId);
    
    List<Event> findByActiveTrue();
    
}
