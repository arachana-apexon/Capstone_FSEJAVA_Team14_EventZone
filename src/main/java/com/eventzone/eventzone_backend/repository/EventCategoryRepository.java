package com.eventzone.eventzone_backend.repository;

import com.eventzone.eventzone_backend.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface EventCategoryRepository extends JpaRepository<EventCategory, UUID> {

    Optional<EventCategory> findByName(String name);

    List<EventCategory> findByActiveTrue();

    Optional<EventCategory> findByIdAndActiveTrue(UUID id);

    Optional<EventCategory> findByNameAndActiveTrue(String name);
}
