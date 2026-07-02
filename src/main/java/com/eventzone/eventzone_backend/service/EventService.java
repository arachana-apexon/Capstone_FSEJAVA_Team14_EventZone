package com.eventzone.eventzone_backend.service;

import com.eventzone.eventzone_backend.constants.Roles;
import com.eventzone.eventzone_backend.dto.EventRequest;
import com.eventzone.eventzone_backend.dto.EventResponse;
import com.eventzone.eventzone_backend.dto.EventStatusResponse;
import com.eventzone.eventzone_backend.entity.Event;
import com.eventzone.eventzone_backend.entity.EventCategory;
import com.eventzone.eventzone_backend.entity.User;
import com.eventzone.eventzone_backend.repository.EventCategoryRepository;
import com.eventzone.eventzone_backend.repository.EventRepository;
import com.eventzone.eventzone_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    
    private final EventRepository eventRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final UserRepository userRepository;
    
    /**
     * Verify that the user has ORGANISER or ADMIN role.
     * Throws RuntimeException if user has ATTENDEE role.
     * 
     * @param user the user to verify
     * @throws RuntimeException if user role is ATTENDEE
     */
    private void verifyUserRole(User user) {

        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }
        
        System.out.println("User Name: " + user.getName());
        System.out.println("User Role: " + user.getRole());
        String userRole = user.getRole();
        if (userRole == null) {
            throw new RuntimeException("User role cannot be null");
        }
        
        // Check if user is ATTENDEE
        if (Roles.ATTENDEE.equals(userRole)) {
            throw new RuntimeException("Access denied");
        }
        
        // Verify user is either ORGANISER or ADMIN
        if (!Roles.ORGANISER.equals(userRole) && !Roles.ADMIN.equals(userRole)) {
            throw new RuntimeException("Access denied");
        }
    }
    
    public EventResponse createEvent(EventRequest request) {
        Objects.requireNonNull(request, "Event request cannot be null");
        
        String title = Objects.requireNonNull(request.getTitle(), "Title cannot be null");
        String description = Objects.requireNonNull(request.getDescription(), "Description cannot be null");
        LocalDateTime eventDate = Objects.requireNonNull(request.getEventDate(), "Event date cannot be null");
        String venue = Objects.requireNonNull(request.getVenue(), "Venue cannot be null");
        
        // Find category by categoryId
        EventCategory category = eventCategoryRepository.findById(Objects.requireNonNull(request.getCategoryId(),
         "Category ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Find organiser by organiserId
        User organiser = userRepository.findById(Objects.requireNonNull(request.getOrganiserId(), "Organiser ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Organiser not found"));
        
        // Verify user role before creating event
        verifyUserRole(organiser);
        
        // Create Event entity
        Event event = Event.builder()
                .title(title)
                .description(description)
                .eventDate(eventDate)
                .venue(venue)
                .coverImageUrl(request.getCoverImageUrl())
                .category(category)
                .organiser(organiser)
                .build();
        
        // Save event
        @SuppressWarnings("null")
        Event savedEvent = Objects.requireNonNull(eventRepository.save(event), "Failed to save event");
        
        // Return EventResponse
        return mapToResponse(savedEvent);
    }
    
    public List<EventResponse> getAllEvents() {
        // Get all active events
        return eventRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public EventResponse getEventById(UUID id) {
        // Find event
        Event event = eventRepository.findById(Objects.requireNonNull(id, "Event ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Return EventResponse
        return mapToResponse(event);
    }
    
    public EventResponse updateEvent(UUID id, EventRequest request) {
        Objects.requireNonNull(request, "Event request cannot be null");
        
        String title = Objects.requireNonNull(request.getTitle(), "Title cannot be null");
        String description = Objects.requireNonNull(request.getDescription(), "Description cannot be null");
        LocalDateTime eventDate = Objects.requireNonNull(request.getEventDate(), "Event date cannot be null");
        String venue = Objects.requireNonNull(request.getVenue(), "Venue cannot be null");
        
        // Find event
        Event event = eventRepository.findById(Objects.requireNonNull(id, "Event ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Find category by categoryId
        EventCategory category = eventCategoryRepository.findById(Objects.requireNonNull(request.getCategoryId(), "Category ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Find organiser by organiserId
        User organiser = userRepository.findById(Objects.requireNonNull(request.getOrganiserId(), "Organiser ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Organiser not found"));
        
        // Verify user role before updating event
        verifyUserRole(organiser);
        
        // Update fields
        event.setTitle(title);
        event.setDescription(description);
        event.setEventDate(eventDate);
        event.setVenue(venue);
        event.setCoverImageUrl(request.getCoverImageUrl());
        event.setCategory(category);
        event.setOrganiser(organiser);
        
        // Save
        Event updatedEvent = eventRepository.save(event);
        
        // Return EventResponse
        return mapToResponse(updatedEvent);
    }
    
    public void deleteEvent(UUID id) {
        // Find event
        Event event = eventRepository.findById(Objects.requireNonNull(id, "Event ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User organiser = Objects.requireNonNull(event.getOrganiser(), "Organiser cannot be null");
        
        // Verify user role before deleting event
        verifyUserRole(organiser);
        
        // Set active = false
        event.setActive(false);
        
        // Save
        eventRepository.save(event);
    }
    
    public EventStatusResponse activeEvent(UUID eventId) {
        // Find event
        Event event = eventRepository.findById(Objects.requireNonNull(eventId, "Event ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        
        // Set active = true
        event.setActive(true);
        
        // Save
        Event savedEvent = eventRepository.save(event);
        
        // Return EventStatusResponse
        return EventStatusResponse.builder()
                .eventId(savedEvent.getId())
                .title(savedEvent.getTitle())
                .active(savedEvent.getActive())
                .message("Event activated successfully")
                .build();
    }
    
    public EventStatusResponse deactiveEvent(UUID eventId) {
        // Find event
        Event event = eventRepository.findById(Objects.requireNonNull(eventId, "Event ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        
        // Set active = false
        event.setActive(false);
        
        // Save
        Event savedEvent = eventRepository.save(event);
        
        // Return EventStatusResponse
        return EventStatusResponse.builder()
                .eventId(savedEvent.getId())
                .title(savedEvent.getTitle())
                .active(savedEvent.getActive())
                .message("Event deactivated successfully")
                .build();
    }
    
    // Private helper method to map Event to EventResponse
    private EventResponse mapToResponse(Event event) {
        Objects.requireNonNull(event, "Event cannot be null");
        
        EventCategory category = Objects.requireNonNull(event.getCategory(), "Category cannot be null");
        User organiser = Objects.requireNonNull(event.getOrganiser(), "Organiser cannot be null");
        
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .venue(event.getVenue())
                .coverImageUrl(event.getCoverImageUrl())
                .categoryName(category.getName())
                .organiserName(organiser.getName())
                .active(event.getActive())
                .build();
    }
    
}
