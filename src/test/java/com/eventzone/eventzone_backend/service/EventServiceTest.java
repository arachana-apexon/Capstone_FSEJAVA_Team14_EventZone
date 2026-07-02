package com.eventzone.eventzone_backend.service;

import com.eventzone.eventzone_backend.dto.EventRequest;
import com.eventzone.eventzone_backend.dto.EventResponse;
import com.eventzone.eventzone_backend.dto.EventStatusResponse;
import com.eventzone.eventzone_backend.entity.Event;
import com.eventzone.eventzone_backend.entity.EventCategory;
import com.eventzone.eventzone_backend.entity.User;
import com.eventzone.eventzone_backend.repository.EventCategoryRepository;
import com.eventzone.eventzone_backend.repository.EventRepository;
import com.eventzone.eventzone_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Tests")
@SuppressWarnings("null")
class EventServiceTest {
    
    @Mock
    private EventRepository eventRepository1;
    
    @Mock
    private EventCategoryRepository eventCategoryRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private EventService eventService;
    
    private UUID eventId;
    private UUID categoryId;
    private UUID adminUserId;
    private UUID organiserUserId;
    private UUID attendeeUserId;
    private LocalDateTime eventDate;
    private EventCategory category;
    private User adminUser;
    private User organiserUser;
    private User attendeeUser;
    private Event event;
    
    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        adminUserId = UUID.randomUUID();
        organiserUserId = UUID.randomUUID();
        attendeeUserId = UUID.randomUUID();
        eventDate = LocalDateTime.now().plusDays(10);
        
        category = EventCategory.builder()
                .id(categoryId)
                .name("Technology")
                .active(true)
                .build();
        
        adminUser = User.builder()
                .id(adminUserId)
                .name("Admin User")
                .email("admin@example.com")
                .role("ADMIN")
                .build();
        
        organiserUser = User.builder()
                .id(organiserUserId)
                .name("Organiser User")
                .email("organiser@example.com")
                .role("ORGANISER")
                .build();
        
        attendeeUser = User.builder()
                .id(attendeeUserId)
                .name("Attendee User")
                .email("attendee@example.com")
                .role("ATTENDEE")
                .build();
        
        event = Event.builder()
                .id(eventId)
                .title("Tech Conference 2026")
                .description("Annual technology conference")
                .eventDate(eventDate)
                .venue("Convention Center")
                .coverImageUrl("https://example.com/image.jpg")
                .category(category)
                .organiser(organiserUser)
                .active(true)
                .build();
    }
    
    @Nested
    @DisplayName("Create Event Tests")
    class CreateEventTests {
        
        @Test
        @DisplayName("Should successfully create event with ORGANISER user")
        void createEvent_shouldSuccessWithOrganiserUser() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .coverImageUrl("https://example.com/image.jpg")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(userRepository.findById(organiserUserId)).thenReturn(Optional.of(organiserUser));
            when(eventRepository1.save(any(Event.class))).thenReturn(event);
            
            // Act
            EventResponse response = eventService.createEvent(request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(eventId, response.getId(), "Event ID should match");
            assertEquals("Tech Conference 2026", response.getTitle(), "Title should match");
            assertEquals("Annual technology conference", response.getDescription(), "Description should match");
            assertEquals("Convention Center", response.getVenue(), "Venue should match");
            assertEquals("Technology", response.getCategoryName(), "Category name should match");
            assertEquals("Organiser User", response.getOrganiserName(), "Organiser name should match");
            assertTrue(response.getActive(), "Event should be active");
            
            verify(eventCategoryRepository, times(1)).findById(categoryId);
            verify(userRepository, times(1)).findById(organiserUserId);
            verify(eventRepository1, times(1)).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should successfully create event with ADMIN user")
        void createEvent_shouldSuccessWithAdminUser() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .coverImageUrl("https://example.com/image.jpg")
                    .categoryId(categoryId)
                    .organiserId(adminUserId)
                    .build();
            
            Event adminEvent = Event.builder()
                    .id(eventId)
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .coverImageUrl("https://example.com/image.jpg")
                    .category(category)
                    .organiser(adminUser)
                    .active(true)
                    .build();
            
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
            when(eventRepository1.save(any(Event.class))).thenReturn(adminEvent);
            
            // Act
            EventResponse response = eventService.createEvent(request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals("Admin User", response.getOrganiserName(), "Organiser should be admin");
            
            verify(eventCategoryRepository, times(1)).findById(categoryId);
            verify(userRepository, times(1)).findById(adminUserId);
            verify(eventRepository1, times(1)).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw exception when organiser is ATTENDEE - Access Denied")
        void createEvent_shouldThrowExceptionWhenOrganiserIsAttendee() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .categoryId(categoryId)
                    .organiserId(attendeeUserId)
                    .build();
            
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(userRepository.findById(attendeeUserId)).thenReturn(Optional.of(attendeeUser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.createEvent(request);
            }, "Should throw RuntimeException when organiser is ATTENDEE");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(eventCategoryRepository, times(1)).findById(categoryId);
            verify(userRepository, times(1)).findById(attendeeUserId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw exception when category not found")
        void createEvent_shouldThrowExceptionWhenCategoryNotFound() {
            // Arrange
            UUID nonExistentCategoryId = UUID.randomUUID();
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .categoryId(nonExistentCategoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            when(eventCategoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.createEvent(request);
            }, "Should throw RuntimeException when category not found");
            
            assertEquals("Category not found", exception.getMessage(), "Exception message should match");
            
            verify(eventCategoryRepository, times(1)).findById(nonExistentCategoryId);
            verify(userRepository, never()).findById(any());
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw exception when organiser not found")
        void createEvent_shouldThrowExceptionWhenOrganiserNotFound() {
            // Arrange
            UUID nonExistentOrganiserId = UUID.randomUUID();
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .categoryId(categoryId)
                    .organiserId(nonExistentOrganiserId)
                    .build();
            
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(userRepository.findById(nonExistentOrganiserId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.createEvent(request);
            }, "Should throw RuntimeException when organiser not found");
            
            assertEquals("Organiser not found", exception.getMessage(), "Exception message should match");
            
            verify(eventCategoryRepository, times(1)).findById(categoryId);
            verify(userRepository, times(1)).findById(nonExistentOrganiserId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when request is null")
        void createEvent_shouldThrowExceptionWhenRequestIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.createEvent(null);
            }, "Should throw NullPointerException when request is null");
            
            assertEquals("Event request cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventCategoryRepository, never()).findById(any());
            verify(userRepository, never()).findById(any());
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when title is null")
        void createEvent_shouldThrowExceptionWhenTitleIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title(null)
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.createEvent(request);
            }, "Should throw NullPointerException when title is null");
            
            assertEquals("Title cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventCategoryRepository, never()).findById(any());
            verify(userRepository, never()).findById(any());
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when description is null")
        void createEvent_shouldThrowExceptionWhenDescriptionIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description(null)
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.createEvent(request);
            }, "Should throw NullPointerException when description is null");
            
            assertEquals("Description cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when event date is null")
        void createEvent_shouldThrowExceptionWhenEventDateIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(null)
                    .venue("Convention Center")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.createEvent(request);
            }, "Should throw NullPointerException when event date is null");
            
            assertEquals("Event date cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when venue is null")
        void createEvent_shouldThrowExceptionWhenVenueIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue(null)
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.createEvent(request);
            }, "Should throw NullPointerException when venue is null");
            
            assertEquals("Venue cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when categoryId is null")
        void createEvent_shouldThrowExceptionWhenCategoryIdIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .categoryId(null)
                    .organiserId(organiserUserId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.createEvent(request);
            }, "Should throw NullPointerException when categoryId is null");
            
            assertEquals("Category ID cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when organiserId is null")
        void createEvent_shouldThrowExceptionWhenOrganiserIdIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .categoryId(categoryId)
                    .organiserId(null)
                    .build();
            
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.createEvent(request);
            }, "Should throw NullPointerException when organiserId is null");
            
            assertEquals("Organiser ID cannot be null", exception.getMessage(), "Exception message should match");
        }
    }
    
    @Nested
    @DisplayName("Get All Events Tests")
    class GetAllEventsTests {
        
        @Test
        @DisplayName("Should successfully return all active events")
        void getAllEvents_shouldReturnAllActiveEvents() {
            // Arrange
            Event event1 = Event.builder()
                    .id(UUID.randomUUID())
                    .title("Event 1")
                    .description("Description 1")
                    .eventDate(eventDate)
                    .venue("Venue 1")
                    .category(category)
                    .organiser(organiserUser)
                    .active(true)
                    .build();
            
            Event event2 = Event.builder()
                    .id(UUID.randomUUID())
                    .title("Event 2")
                    .description("Description 2")
                    .eventDate(eventDate)
                    .venue("Venue 2")
                    .category(category)
                    .organiser(organiserUser)
                    .active(true)
                    .build();
            
            List<Event> activeEvents = Arrays.asList(event1, event2);
            
            when(eventRepository1.findByActiveTrue()).thenReturn(activeEvents);
            
            // Act
            List<EventResponse> responses = eventService.getAllEvents();
            
            // Assert
            assertNotNull(responses, "Response list should not be null");
            assertEquals(2, responses.size(), "Should return 2 active events");
            assertEquals("Event 1", responses.get(0).getTitle(), "First event title should match");
            assertEquals("Event 2", responses.get(1).getTitle(), "Second event title should match");
            
            verify(eventRepository1, times(1)).findByActiveTrue();
        }
        
        @Test
        @DisplayName("Should return empty list when no active events exist")
        void getAllEvents_shouldReturnEmptyListWhenNoActiveEvents() {
            // Arrange
            when(eventRepository1.findByActiveTrue()).thenReturn(Collections.emptyList());
            
            // Act
            List<EventResponse> responses = eventService.getAllEvents();
            
            // Assert
            assertNotNull(responses, "Response list should not be null");
            assertEquals(0, responses.size(), "Should return empty list");
            
            verify(eventRepository1, times(1)).findByActiveTrue();
        }
    }
    
    @Nested
    @DisplayName("Get Event By ID Tests")
    class GetEventByIdTests {
        
        @Test
        @DisplayName("Should successfully return event by ID")
        void getEventById_shouldReturnEventSuccessfully() {
            // Arrange
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(event));
            
            // Act
            EventResponse response = eventService.getEventById(eventId);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(eventId, response.getId(), "Event ID should match");
            assertEquals("Tech Conference 2026", response.getTitle(), "Title should match");
            assertEquals("Annual technology conference", response.getDescription(), "Description should match");
            assertEquals("Convention Center", response.getVenue(), "Venue should match");
            
            verify(eventRepository1, times(1)).findById(eventId);
        }
        
        @Test
        @DisplayName("Should throw exception when event not found")
        void getEventById_shouldThrowExceptionWhenEventNotFound() {
            // Arrange
            UUID nonExistentEventId = UUID.randomUUID();
            when(eventRepository1.findById(nonExistentEventId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.getEventById(nonExistentEventId);
            }, "Should throw RuntimeException when event not found");
            
            assertEquals("Event not found", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, times(1)).findById(nonExistentEventId);
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when event ID is null")
        void getEventById_shouldThrowExceptionWhenEventIdIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.getEventById(null);
            }, "Should throw NullPointerException when event ID is null");
            
            assertEquals("Event ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, never()).findById(any());
        }
    }
    
    @Nested
    @DisplayName("Update Event Tests")
    class UpdateEventTests {
        
        @Test
        @DisplayName("Should successfully update event with ORGANISER user")
        void updateEvent_shouldSuccessWithOrganiserUser() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .coverImageUrl("https://example.com/new-image.jpg")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            Event updatedEvent = Event.builder()
                    .id(eventId)
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .coverImageUrl("https://example.com/new-image.jpg")
                    .category(category)
                    .organiser(organiserUser)
                    .active(true)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(event));
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(userRepository.findById(organiserUserId)).thenReturn(Optional.of(organiserUser));
            when(eventRepository1.save(any(Event.class))).thenReturn(updatedEvent);
            
            // Act
            EventResponse response = eventService.updateEvent(eventId, request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals("Updated Tech Conference 2026", response.getTitle(), "Title should be updated");
            assertEquals("Updated description", response.getDescription(), "Description should be updated");
            assertEquals("Updated Venue", response.getVenue(), "Venue should be updated");
            
            verify(eventRepository1, times(1)).findById(eventId);
            verify(eventCategoryRepository, times(1)).findById(categoryId);
            verify(userRepository, times(1)).findById(organiserUserId);
            verify(eventRepository1, times(1)).save(event);
        }
        
        @Test
        @DisplayName("Should throw exception when event not found")
        void updateEvent_shouldThrowExceptionWhenEventNotFound() {
            // Arrange
            UUID nonExistentEventId = UUID.randomUUID();
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            when(eventRepository1.findById(nonExistentEventId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.updateEvent(nonExistentEventId, request);
            }, "Should throw RuntimeException when event not found");
            
            assertEquals("Event not found", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, times(1)).findById(nonExistentEventId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw exception when organiser is ATTENDEE - Access Denied")
        void updateEvent_shouldThrowExceptionWhenOrganiserIsAttendee() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .categoryId(categoryId)
                    .organiserId(attendeeUserId)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(event));
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(userRepository.findById(attendeeUserId)).thenReturn(Optional.of(attendeeUser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.updateEvent(eventId, request);
            }, "Should throw RuntimeException when organiser is ATTENDEE");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, times(1)).findById(eventId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when event ID is null")
        void updateEvent_shouldThrowExceptionWhenEventIdIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.updateEvent(null, request);
            }, "Should throw NullPointerException when event ID is null");
            
            assertEquals("Event ID cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when request is null")
        void updateEvent_shouldThrowExceptionWhenRequestIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.updateEvent(eventId, null);
            }, "Should throw NullPointerException when request is null");
            
            assertEquals("Event request cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when title is null in update request")
        void updateEvent_shouldThrowExceptionWhenTitleIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title(null)
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.updateEvent(eventId, request);
            }, "Should throw NullPointerException when title is null");
            
            assertEquals("Title cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when description is null in update request")
        void updateEvent_shouldThrowExceptionWhenDescriptionIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description(null)
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.updateEvent(eventId, request);
            }, "Should throw NullPointerException when description is null");
            
            assertEquals("Description cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when event date is null in update request")
        void updateEvent_shouldThrowExceptionWhenEventDateIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(null)
                    .venue("Updated Venue")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.updateEvent(eventId, request);
            }, "Should throw NullPointerException when event date is null");
            
            assertEquals("Event date cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when venue is null in update request")
        void updateEvent_shouldThrowExceptionWhenVenueIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue(null)
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.updateEvent(eventId, request);
            }, "Should throw NullPointerException when venue is null");
            
            assertEquals("Venue cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when categoryId is null in update request")
        void updateEvent_shouldThrowExceptionWhenCategoryIdIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .categoryId(null)
                    .organiserId(organiserUserId)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(event));
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.updateEvent(eventId, request);
            }, "Should throw NullPointerException when categoryId is null");
            
            assertEquals("Category ID cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when organiserId is null in update request")
        void updateEvent_shouldThrowExceptionWhenOrganiserIdIsNull() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .categoryId(categoryId)
                    .organiserId(null)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(event));
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.updateEvent(eventId, request);
            }, "Should throw NullPointerException when organiserId is null");
            
            assertEquals("Organiser ID cannot be null", exception.getMessage(), "Exception message should match");
        }
    }
    
    @Nested
    @DisplayName("Delete Event Tests")
    class DeleteEventTests {
        
        @Test
        @DisplayName("Should successfully soft delete event with ORGANISER user")
        void deleteEvent_shouldSuccessWithOrganiserUser() {
            // Arrange
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(event));
            when(eventRepository1.save(any(Event.class))).thenReturn(event);
            
            // Act
            eventService.deleteEvent(eventId);
            
            // Assert
            assertFalse(event.getActive(), "Event should be soft deleted (inactive)");
            
            verify(eventRepository1, times(1)).findById(eventId);
            verify(eventRepository1, times(1)).save(event);
        }
        
        @Test
        @DisplayName("Should throw exception when event not found")
        void deleteEvent_shouldThrowExceptionWhenEventNotFound() {
            // Arrange
            UUID nonExistentEventId = UUID.randomUUID();
            when(eventRepository1.findById(nonExistentEventId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.deleteEvent(nonExistentEventId);
            }, "Should throw RuntimeException when event not found");
            
            assertEquals("Event not found", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, times(1)).findById(nonExistentEventId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw exception when organiser is ATTENDEE - Access Denied")
        void deleteEvent_shouldThrowExceptionWhenOrganiserIsAttendee() {
            // Arrange
            Event attendeeEvent = Event.builder()
                    .id(eventId)
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .category(category)
                    .organiser(attendeeUser)
                    .active(true)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(attendeeEvent));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.deleteEvent(eventId);
            }, "Should throw RuntimeException when organiser is ATTENDEE");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, times(1)).findById(eventId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when event ID is null")
        void deleteEvent_shouldThrowExceptionWhenEventIdIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.deleteEvent(null);
            }, "Should throw NullPointerException when event ID is null");
            
            assertEquals("Event ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, never()).findById(any());
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw RuntimeException when organiser reference is null")
        void deleteEvent_shouldThrowExceptionWhenOrganiserIsNull() {
            // Arrange
            Event eventWithNullOrganiser = Event.builder()
                    .id(eventId)
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .category(category)
                    .organiser(null)  // null organiser
                    .active(true)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(eventWithNullOrganiser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.deleteEvent(eventId);
            }, "Should throw RuntimeException when organiser is null");
            
            assertEquals("Organiser cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, times(1)).findById(eventId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
    }
    
    @Nested
    @DisplayName("Activate Event Tests")
    class ActivateEventTests {
        
        @Test
        @DisplayName("Should successfully activate event")
        void activeEvent_shouldActivateEventSuccessfully() {
            // Arrange
            Event inactiveEvent = Event.builder()
                    .id(eventId)
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .category(category)
                    .organiser(organiserUser)
                    .active(false)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(inactiveEvent));
            when(eventRepository1.save(any(Event.class))).thenReturn(inactiveEvent);
            
            // Act
            EventStatusResponse response = eventService.activeEvent(eventId);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(eventId, response.getEventId(), "Event ID should match");
            assertEquals("Tech Conference 2026", response.getTitle(), "Title should match");
            assertTrue(response.getActive(), "Event should be active");
            assertEquals("Event activated successfully", response.getMessage(), "Message should match");
            assertTrue(inactiveEvent.getActive(), "Event active status should be true");
            
            verify(eventRepository1, times(1)).findById(eventId);
            verify(eventRepository1, times(1)).save(inactiveEvent);
        }
        
        @Test
        @DisplayName("Should throw exception when event not found")
        void activeEvent_shouldThrowExceptionWhenEventNotFound() {
            // Arrange
            UUID nonExistentEventId = UUID.randomUUID();
            when(eventRepository1.findById(nonExistentEventId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.activeEvent(nonExistentEventId);
            }, "Should throw RuntimeException when event not found");
            
            assertTrue(exception.getMessage().contains("Event not found"), "Exception message should contain 'Event not found'");
            
            verify(eventRepository1, times(1)).findById(nonExistentEventId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when event ID is null")
        void activeEvent_shouldThrowExceptionWhenEventIdIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.activeEvent(null);
            }, "Should throw NullPointerException when event ID is null");
            
            assertEquals("Event ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, never()).findById(any());
            verify(eventRepository1, never()).save(any(Event.class));
        }
    }
    
    @Nested
    @DisplayName("Deactivate Event Tests")
    class DeactivateEventTests {
        
        @Test
        @DisplayName("Should successfully deactivate event")
        void deactiveEvent_shouldDeactivateEventSuccessfully() {
            // Arrange
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(event));
            when(eventRepository1.save(any(Event.class))).thenReturn(event);
            
            // Act
            EventStatusResponse response = eventService.deactiveEvent(eventId);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(eventId, response.getEventId(), "Event ID should match");
            assertEquals("Tech Conference 2026", response.getTitle(), "Title should match");
            assertFalse(response.getActive(), "Event should be inactive");
            assertEquals("Event deactivated successfully", response.getMessage(), "Message should match");
            assertFalse(event.getActive(), "Event active status should be false");
            
            verify(eventRepository1, times(1)).findById(eventId);
            verify(eventRepository1, times(1)).save(event);
        }
        
        @Test
        @DisplayName("Should throw exception when event not found")
        void deactiveEvent_shouldThrowExceptionWhenEventNotFound() {
            // Arrange
            UUID nonExistentEventId = UUID.randomUUID();
            when(eventRepository1.findById(nonExistentEventId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.deactiveEvent(nonExistentEventId);
            }, "Should throw RuntimeException when event not found");
            
            assertTrue(exception.getMessage().contains("Event not found"), "Exception message should contain 'Event not found'");
            
            verify(eventRepository1, times(1)).findById(nonExistentEventId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when event ID is null")
        void deactiveEvent_shouldThrowExceptionWhenEventIdIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                eventService.deactiveEvent(null);
            }, "Should throw NullPointerException when event ID is null");
            
            assertEquals("Event ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, never()).findById(any());
            verify(eventRepository1, never()).save(any(Event.class));
        }
    }
    
    @Nested
    @DisplayName("User Role Verification Tests")
    class UserRoleVerificationTests {
        
        @Test
        @DisplayName("Should throw RuntimeException when user role is null")
        void verifyUserRole_shouldThrowExceptionWhenRoleIsNull() {
            // Arrange
            User userWithNullRole = User.builder()
                    .id(organiserUserId)
                    .name("User with Null Role")
                    .email("nullrole@example.com")
                    .role(null)  // null role
                    .build();
            
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(userRepository.findById(organiserUserId)).thenReturn(Optional.of(userWithNullRole));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.createEvent(request);
            }, "Should throw RuntimeException when user role is null");
            
            assertEquals("User role cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventCategoryRepository, times(1)).findById(categoryId);
            verify(userRepository, times(1)).findById(organiserUserId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw RuntimeException when user role is invalid")
        void verifyUserRole_shouldThrowExceptionWhenRoleIsInvalid() {
            // Arrange
            User userWithInvalidRole = User.builder()
                    .id(organiserUserId)
                    .name("User with Invalid Role")
                    .email("invalidrole@example.com")
                    .role("INVALID_ROLE")  // invalid role
                    .build();
            
            EventRequest request = EventRequest.builder()
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .categoryId(categoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(userRepository.findById(organiserUserId)).thenReturn(Optional.of(userWithInvalidRole));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.createEvent(request);
            }, "Should throw RuntimeException when user role is invalid");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(eventCategoryRepository, times(1)).findById(categoryId);
            verify(userRepository, times(1)).findById(organiserUserId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw RuntimeException when user role is ATTENDEE in update")
        void verifyUserRole_shouldThrowExceptionForAttendeeInUpdate() {
            // Arrange
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .categoryId(categoryId)
                    .organiserId(attendeeUserId)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(event));
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(userRepository.findById(attendeeUserId)).thenReturn(Optional.of(attendeeUser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.updateEvent(eventId, request);
            }, "Should throw RuntimeException when organiser is ATTENDEE");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw RuntimeException when user role is ATTENDEE in delete")
        void verifyUserRole_shouldThrowExceptionForAttendeeInDelete() {
            // Arrange
            Event attendeeEvent = Event.builder()
                    .id(eventId)
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .category(category)
                    .organiser(attendeeUser)
                    .active(true)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(attendeeEvent));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.deleteEvent(eventId);
            }, "Should throw RuntimeException when organiser is ATTENDEE");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, never()).save(any(Event.class));
        }
    }
    
    @Nested
    @DisplayName("Entity Reference Null Safety Tests")
    class EntityReferenceNullSafetyTests {
        
        @Test
        @DisplayName("Should throw RuntimeException when event category is null in mapToResponse")
        void mapToResponse_shouldThrowExceptionWhenCategoryIsNull() {
            // Arrange
            Event eventWithNullCategory = Event.builder()
                    .id(eventId)
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .category(null)  // null category
                    .organiser(organiserUser)
                    .active(true)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(eventWithNullCategory));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.getEventById(eventId);
            }, "Should throw RuntimeException when category is null");
            
            assertEquals("Category cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, times(1)).findById(eventId);
        }
        
        @Test
        @DisplayName("Should throw RuntimeException when event organiser is null in mapToResponse")
        void mapToResponse_shouldThrowExceptionWhenOrganiserIsNull() {
            // Arrange
            Event eventWithNullOrganiser = Event.builder()
                    .id(eventId)
                    .title("Tech Conference 2026")
                    .description("Annual technology conference")
                    .eventDate(eventDate)
                    .venue("Convention Center")
                    .category(category)
                    .organiser(null)  // null organiser
                    .active(true)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(eventWithNullOrganiser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.getEventById(eventId);
            }, "Should throw RuntimeException when organiser is null");
            
            assertEquals("Organiser cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, times(1)).findById(eventId);
        }
        
        @Test
        @DisplayName("Should throw RuntimeException when updating event with non-existent category")
        void updateEvent_shouldThrowExceptionWhenCategoryNotFound() {
            // Arrange
            UUID nonExistentCategoryId = UUID.randomUUID();
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .categoryId(nonExistentCategoryId)
                    .organiserId(organiserUserId)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(event));
            when(eventCategoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.updateEvent(eventId, request);
            }, "Should throw RuntimeException when category not found");
            
            assertEquals("Category not found", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, times(1)).findById(eventId);
            verify(eventCategoryRepository, times(1)).findById(nonExistentCategoryId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Should throw RuntimeException when updating event with non-existent organiser")
        void updateEvent_shouldThrowExceptionWhenOrganiserNotFound() {
            // Arrange
            UUID nonExistentOrganiserId = UUID.randomUUID();
            EventRequest request = EventRequest.builder()
                    .title("Updated Tech Conference 2026")
                    .description("Updated description")
                    .eventDate(eventDate)
                    .venue("Updated Venue")
                    .categoryId(categoryId)
                    .organiserId(nonExistentOrganiserId)
                    .build();
            
            when(eventRepository1.findById(eventId)).thenReturn(Optional.of(event));
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(userRepository.findById(nonExistentOrganiserId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.updateEvent(eventId, request);
            }, "Should throw RuntimeException when organiser not found");
            
            assertEquals("Organiser not found", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository1, times(1)).findById(eventId);
            verify(eventCategoryRepository, times(1)).findById(categoryId);
            verify(userRepository, times(1)).findById(nonExistentOrganiserId);
            verify(eventRepository1, never()).save(any(Event.class));
        }
    }
}
