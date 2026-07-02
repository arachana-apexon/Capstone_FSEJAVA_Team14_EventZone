package com.eventzone.eventzone_backend.controller;

import com.eventzone.eventzone_backend.dto.EventRequest;
import com.eventzone.eventzone_backend.dto.EventResponse;
import com.eventzone.eventzone_backend.dto.EventStatusResponse;
import com.eventzone.eventzone_backend.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable UUID id) {
        EventResponse response = eventService.getEventById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.updateEvent(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/active")
    public ResponseEntity<EventStatusResponse> activeEvent(@PathVariable UUID id) {
        EventStatusResponse response = eventService.activeEvent(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/deactive")
    public ResponseEntity<EventStatusResponse> deactiveEvent(@PathVariable UUID id) {
        EventStatusResponse response = eventService.deactiveEvent(id);
        return ResponseEntity.ok(response);
    }
}
