package com.eventzone.eventzone_backend.service;

import com.eventzone.eventzone_backend.dto.BookingSummaryResponse;
import com.eventzone.eventzone_backend.dto.TicketCategoryRequest;
import com.eventzone.eventzone_backend.dto.TicketCategoryResponse;
import com.eventzone.eventzone_backend.entity.Event;
import com.eventzone.eventzone_backend.entity.TicketCategory;
import com.eventzone.eventzone_backend.repository.BookingRepository;
import com.eventzone.eventzone_backend.repository.EventRepository;
import com.eventzone.eventzone_backend.repository.TicketCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TicketCategoryService Tests")
@SuppressWarnings("null")
class TicketCategoryServiceTest {
    
    @Mock
    private TicketCategoryRepository ticketCategoryRepository;
    
    @Mock
    private EventRepository eventRepository;
    
    @Mock
    private BookingRepository bookingRepository;
    
    @InjectMocks
    private TicketCategoryService ticketCategoryService;
    
    private UUID ticketCategoryId;
    private UUID eventId;
    private Event event;
    private TicketCategory ticketCategory;
    
    @BeforeEach
    void setUp() {
        ticketCategoryId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        
        event = Event.builder()
                .id(eventId)
                .title("Tech Conference 2026")
                .description("Annual technology conference")
                .active(true)
                .build();
        
        ticketCategory = TicketCategory.builder()
                .id(ticketCategoryId)
                .name("VIP")
                .price(new BigDecimal("100.00"))
                .totalSeats(100)
                .availableSeats(100)
                .event(event)
                .build();
    }
    
    @Nested
    @DisplayName("Create Ticket Category Tests")
    class CreateTicketCategoryTests {
        
        @Test
        @DisplayName("Should successfully create ticket category")
        void createTicketCategory_shouldSuccessfullyCreateTicketCategory() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("VIP")
                    .price(new BigDecimal("100.00"))
                    .totalSeats(100)
                    .eventId(eventId)
                    .build();
            
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(ticketCategoryRepository.save(any(TicketCategory.class))).thenReturn(ticketCategory);
            
            // Act
            TicketCategoryResponse response = ticketCategoryService.createTicketCategory(request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(ticketCategoryId, response.getId(), "Ticket category ID should match");
            assertEquals("VIP", response.getName(), "Name should match");
            assertEquals(new BigDecimal("100.00"), response.getPrice(), "Price should match");
            assertEquals(100, response.getTotalSeats(), "Total seats should match");
            assertEquals(100, response.getAvailableSeats(), "Available seats should equal total seats");
            assertEquals("Tech Conference 2026", response.getEventTitle(), "Event title should match");
            
            verify(eventRepository, times(1)).findById(eventId);
            verify(ticketCategoryRepository, times(1)).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when event not found")
        void createTicketCategory_shouldThrowExceptionWhenEventNotFound() {
            // Arrange
            UUID nonExistentEventId = UUID.randomUUID();
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("VIP")
                    .price(new BigDecimal("100.00"))
                    .totalSeats(100)
                    .eventId(nonExistentEventId)
                    .build();
            
            when(eventRepository.findById(nonExistentEventId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketCategoryService.createTicketCategory(request);
            }, "Should throw RuntimeException when event not found");
            
            assertTrue(exception.getMessage().contains("Event not found"), "Exception message should contain 'Event not found'");
            
            verify(eventRepository, times(1)).findById(nonExistentEventId);
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when request is null")
        void createTicketCategory_shouldThrowExceptionWhenRequestIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.createTicketCategory(null);
            }, "Should throw NullPointerException when request is null");
            
            assertEquals("Ticket category request cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when name is null")
        void createTicketCategory_shouldThrowExceptionWhenNameIsNull() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name(null)
                    .price(new BigDecimal("100.00"))
                    .totalSeats(100)
                    .eventId(eventId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.createTicketCategory(request);
            }, "Should throw NullPointerException when name is null");
            
            assertEquals("Name cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when price is null")
        void createTicketCategory_shouldThrowExceptionWhenPriceIsNull() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("VIP")
                    .price(null)
                    .totalSeats(100)
                    .eventId(eventId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.createTicketCategory(request);
            }, "Should throw NullPointerException when price is null");
            
            assertEquals("Price cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when totalSeats is null")
        void createTicketCategory_shouldThrowExceptionWhenTotalSeatsIsNull() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("VIP")
                    .price(new BigDecimal("100.00"))
                    .totalSeats(null)
                    .eventId(eventId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.createTicketCategory(request);
            }, "Should throw NullPointerException when totalSeats is null");
            
            assertEquals("Total seats cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when eventId is null")
        void createTicketCategory_shouldThrowExceptionWhenEventIdIsNull() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("VIP")
                    .price(new BigDecimal("100.00"))
                    .totalSeats(100)
                    .eventId(null)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.createTicketCategory(request);
            }, "Should throw NullPointerException when eventId is null");
            
            assertEquals("Event ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when totalSeats is zero")
        void createTicketCategory_shouldThrowExceptionWhenTotalSeatsIsZero() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("VIP")
                    .price(new BigDecimal("100.00"))
                    .totalSeats(0)
                    .eventId(eventId)
                    .build();
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketCategoryService.createTicketCategory(request);
            }, "Should throw RuntimeException when totalSeats is zero");
            
            assertEquals("Total seats must be greater than 0", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when totalSeats is negative")
        void createTicketCategory_shouldThrowExceptionWhenTotalSeatsIsNegative() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("VIP")
                    .price(new BigDecimal("100.00"))
                    .totalSeats(-10)
                    .eventId(eventId)
                    .build();
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketCategoryService.createTicketCategory(request);
            }, "Should throw RuntimeException when totalSeats is negative");
            
            assertEquals("Total seats must be greater than 0", exception.getMessage(), "Exception message should match");
            
            verify(eventRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
    }
    
    @Nested
    @DisplayName("Get Ticket Categories By Event Tests")
    class GetTicketCategoriesByEventTests {
        
        @Test
        @DisplayName("Should successfully return all ticket categories for event")
        void getTicketCategoriesByEvent_shouldReturnAllTicketCategories() {
            // Arrange
            TicketCategory ticketCategory1 = TicketCategory.builder()
                    .id(UUID.randomUUID())
                    .name("VIP")
                    .price(new BigDecimal("100.00"))
                    .totalSeats(100)
                    .availableSeats(80)
                    .event(event)
                    .build();
            
            TicketCategory ticketCategory2 = TicketCategory.builder()
                    .id(UUID.randomUUID())
                    .name("General")
                    .price(new BigDecimal("50.00"))
                    .totalSeats(200)
                    .availableSeats(150)
                    .event(event)
                    .build();
            
            List<TicketCategory> ticketCategories = Arrays.asList(ticketCategory1, ticketCategory2);
            
            when(ticketCategoryRepository.findByEventId(eventId)).thenReturn(ticketCategories);
            
            // Act
            List<TicketCategoryResponse> responses = ticketCategoryService.getTicketCategoriesByEvent(eventId);
            
            // Assert
            assertNotNull(responses, "Response list should not be null");
            assertEquals(2, responses.size(), "Should return 2 ticket categories");
            
            assertEquals("VIP", responses.get(0).getName(), "First ticket category name should match");
            assertEquals(new BigDecimal("100.00"), responses.get(0).getPrice(), "First ticket category price should match");
            assertEquals(100, responses.get(0).getTotalSeats(), "First ticket category total seats should match");
            assertEquals(80, responses.get(0).getAvailableSeats(), "First ticket category available seats should match");
            
            assertEquals("General", responses.get(1).getName(), "Second ticket category name should match");
            assertEquals(new BigDecimal("50.00"), responses.get(1).getPrice(), "Second ticket category price should match");
            assertEquals(200, responses.get(1).getTotalSeats(), "Second ticket category total seats should match");
            assertEquals(150, responses.get(1).getAvailableSeats(), "Second ticket category available seats should match");
            
            verify(ticketCategoryRepository, times(1)).findByEventId(eventId);
        }
        
        @Test
        @DisplayName("Should return empty list when event has no ticket categories")
        void getTicketCategoriesByEvent_shouldReturnEmptyListWhenNoTicketCategories() {
            // Arrange
            when(ticketCategoryRepository.findByEventId(eventId)).thenReturn(Collections.emptyList());
            
            // Act
            List<TicketCategoryResponse> responses = ticketCategoryService.getTicketCategoriesByEvent(eventId);
            
            // Assert
            assertNotNull(responses, "Response list should not be null");
            assertEquals(0, responses.size(), "Should return empty list");
            
            verify(ticketCategoryRepository, times(1)).findByEventId(eventId);
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when eventId is null")
        void getTicketCategoriesByEvent_shouldThrowExceptionWhenEventIdIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.getTicketCategoriesByEvent(null);
            }, "Should throw NullPointerException when eventId is null");
            
            assertEquals("Event ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(ticketCategoryRepository, never()).findByEventId(any());
        }
    }
    
    @Nested
    @DisplayName("Update Ticket Category Tests")
    class UpdateTicketCategoryTests {
        
        @Test
        @DisplayName("Should successfully update ticket category")
        void updateTicketCategory_shouldSuccessfullyUpdateTicketCategory() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("Premium VIP")
                    .price(new BigDecimal("150.00"))
                    .totalSeats(120)
                    .eventId(eventId)
                    .build();
            
            TicketCategory updatedTicketCategory = TicketCategory.builder()
                    .id(ticketCategoryId)
                    .name("Premium VIP")
                    .price(new BigDecimal("150.00"))
                    .totalSeats(120)
                    .availableSeats(100)
                    .event(event)
                    .build();
            
            when(ticketCategoryRepository.findById(ticketCategoryId)).thenReturn(Optional.of(ticketCategory));
            when(ticketCategoryRepository.save(any(TicketCategory.class))).thenReturn(updatedTicketCategory);
            
            // Act
            TicketCategoryResponse response = ticketCategoryService.updateTicketCategory(ticketCategoryId, request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(ticketCategoryId, response.getId(), "Ticket category ID should match");
            assertEquals("Premium VIP", response.getName(), "Name should be updated");
            assertEquals(new BigDecimal("150.00"), response.getPrice(), "Price should be updated");
            assertEquals(120, response.getTotalSeats(), "Total seats should be updated");
            assertEquals("Tech Conference 2026", response.getEventTitle(), "Event title should match");
            
            verify(ticketCategoryRepository, times(1)).findById(ticketCategoryId);
            verify(ticketCategoryRepository, times(1)).save(ticketCategory);
        }
        
        @Test
        @DisplayName("Should throw exception when ticket category not found")
        void updateTicketCategory_shouldThrowExceptionWhenTicketCategoryNotFound() {
            // Arrange
            UUID nonExistentTicketCategoryId = UUID.randomUUID();
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("Premium VIP")
                    .price(new BigDecimal("150.00"))
                    .totalSeats(120)
                    .eventId(eventId)
                    .build();
            
            when(ticketCategoryRepository.findById(nonExistentTicketCategoryId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketCategoryService.updateTicketCategory(nonExistentTicketCategoryId, request);
            }, "Should throw RuntimeException when ticket category not found");
            
            assertTrue(exception.getMessage().contains("Ticket category not found"), "Exception message should contain 'Ticket category not found'");
            
            verify(ticketCategoryRepository, times(1)).findById(nonExistentTicketCategoryId);
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when id is null")
        void updateTicketCategory_shouldThrowExceptionWhenIdIsNull() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("Premium VIP")
                    .price(new BigDecimal("150.00"))
                    .totalSeats(120)
                    .eventId(eventId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.updateTicketCategory(null, request);
            }, "Should throw NullPointerException when id is null");
            
            assertEquals("Ticket category ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(ticketCategoryRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when request is null")
        void updateTicketCategory_shouldThrowExceptionWhenRequestIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.updateTicketCategory(ticketCategoryId, null);
            }, "Should throw NullPointerException when request is null");
            
            assertEquals("Ticket category request cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(ticketCategoryRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when name is null")
        void updateTicketCategory_shouldThrowExceptionWhenNameIsNull() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name(null)
                    .price(new BigDecimal("150.00"))
                    .totalSeats(120)
                    .eventId(eventId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.updateTicketCategory(ticketCategoryId, request);
            }, "Should throw NullPointerException when name is null");
            
            assertEquals("Name cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when price is null")
        void updateTicketCategory_shouldThrowExceptionWhenPriceIsNull() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("Premium VIP")
                    .price(null)
                    .totalSeats(120)
                    .eventId(eventId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.updateTicketCategory(ticketCategoryId, request);
            }, "Should throw NullPointerException when price is null");
            
            assertEquals("Price cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when totalSeats is null")
        void updateTicketCategory_shouldThrowExceptionWhenTotalSeatsIsNull() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("Premium VIP")
                    .price(new BigDecimal("150.00"))
                    .totalSeats(null)
                    .eventId(eventId)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.updateTicketCategory(ticketCategoryId, request);
            }, "Should throw NullPointerException when totalSeats is null");
            
            assertEquals("Total seats cannot be null", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw exception when totalSeats is zero")
        void updateTicketCategory_shouldThrowExceptionWhenTotalSeatsIsZero() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("Premium VIP")
                    .price(new BigDecimal("150.00"))
                    .totalSeats(0)
                    .eventId(eventId)
                    .build();
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketCategoryService.updateTicketCategory(ticketCategoryId, request);
            }, "Should throw RuntimeException when totalSeats is zero");
            
            assertEquals("Total seats must be greater than 0", exception.getMessage(), "Exception message should match");
        }
        
        @Test
        @DisplayName("Should throw exception when totalSeats is negative")
        void updateTicketCategory_shouldThrowExceptionWhenTotalSeatsIsNegative() {
            // Arrange
            TicketCategoryRequest request = TicketCategoryRequest.builder()
                    .name("Premium VIP")
                    .price(new BigDecimal("150.00"))
                    .totalSeats(-10)
                    .eventId(eventId)
                    .build();
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketCategoryService.updateTicketCategory(ticketCategoryId, request);
            }, "Should throw RuntimeException when totalSeats is negative");
            
            assertEquals("Total seats must be greater than 0", exception.getMessage(), "Exception message should match");
        }
    }
    
    @Nested
    @DisplayName("Delete Ticket Category Tests")
    class DeleteTicketCategoryTests {
        
        @Test
        @DisplayName("Should successfully delete ticket category")
        void deleteTicketCategory_shouldSuccessfullyDeleteTicketCategory() {
            // Arrange
            doNothing().when(ticketCategoryRepository).deleteById(ticketCategoryId);
            
            // Act
            ticketCategoryService.deleteTicketCategory(ticketCategoryId);
            
            // Assert
            verify(ticketCategoryRepository, times(1)).deleteById(ticketCategoryId);
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when id is null")
        void deleteTicketCategory_shouldThrowExceptionWhenIdIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.deleteTicketCategory(null);
            }, "Should throw NullPointerException when id is null");
            
            assertEquals("Ticket category ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(ticketCategoryRepository, never()).deleteById(any());
        }
    }
    
    @Nested
    @DisplayName("Get Booking Summary By Event Tests")
    class GetBookingSummaryByEventTests {
        
        @Test
        @DisplayName("Should successfully return booking summary for event")
        void getBookingSummaryByEvent_shouldReturnBookingSummary() {
            // Arrange
            TicketCategory ticketCategory1 = TicketCategory.builder()
                    .id(UUID.randomUUID())
                    .name("VIP")
                    .price(new BigDecimal("100.00"))
                    .totalSeats(100)
                    .availableSeats(80)
                    .event(event)
                    .build();
            
            TicketCategory ticketCategory2 = TicketCategory.builder()
                    .id(UUID.randomUUID())
                    .name("General")
                    .price(new BigDecimal("50.00"))
                    .totalSeats(200)
                    .availableSeats(150)
                    .event(event)
                    .build();
            
            List<TicketCategory> ticketCategories = Arrays.asList(ticketCategory1, ticketCategory2);
            
            when(ticketCategoryRepository.findByEventId(eventId)).thenReturn(ticketCategories);
            when(bookingRepository.countByTicketCategoryId(ticketCategory1.getId())).thenReturn(20L);
            when(bookingRepository.countByTicketCategoryId(ticketCategory2.getId())).thenReturn(50L);
            
            // Act
            List<BookingSummaryResponse> responses = ticketCategoryService.getBookingSummaryByEvent(eventId);
            
            // Assert
            assertNotNull(responses, "Response list should not be null");
            assertEquals(2, responses.size(), "Should return 2 booking summaries");
            
            assertEquals("VIP", responses.get(0).getTicketCategoryName(), "First ticket category name should match");
            assertEquals(100, responses.get(0).getTotalSeats(), "First ticket category total seats should match");
            assertEquals(80, responses.get(0).getAvailableSeats(), "First ticket category available seats should match");
            assertEquals(20, responses.get(0).getBookedSeats(), "First ticket category booked seats should match");
            
            assertEquals("General", responses.get(1).getTicketCategoryName(), "Second ticket category name should match");
            assertEquals(200, responses.get(1).getTotalSeats(), "Second ticket category total seats should match");
            assertEquals(150, responses.get(1).getAvailableSeats(), "Second ticket category available seats should match");
            assertEquals(50, responses.get(1).getBookedSeats(), "Second ticket category booked seats should match");
            
            verify(ticketCategoryRepository, times(1)).findByEventId(eventId);
            verify(bookingRepository, times(1)).countByTicketCategoryId(ticketCategory1.getId());
            verify(bookingRepository, times(1)).countByTicketCategoryId(ticketCategory2.getId());
        }
        
        @Test
        @DisplayName("Should return empty list when event has no ticket categories")
        void getBookingSummaryByEvent_shouldReturnEmptyListWhenNoTicketCategories() {
            // Arrange
            when(ticketCategoryRepository.findByEventId(eventId)).thenReturn(Collections.emptyList());
            
            // Act
            List<BookingSummaryResponse> responses = ticketCategoryService.getBookingSummaryByEvent(eventId);
            
            // Assert
            assertNotNull(responses, "Response list should not be null");
            assertEquals(0, responses.size(), "Should return empty list");
            
            verify(ticketCategoryRepository, times(1)).findByEventId(eventId);
            verify(bookingRepository, never()).countByTicketCategoryId(any());
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when eventId is null")
        void getBookingSummaryByEvent_shouldThrowExceptionWhenEventIdIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                ticketCategoryService.getBookingSummaryByEvent(null);
            }, "Should throw NullPointerException when eventId is null");
            
            assertEquals("Event ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(ticketCategoryRepository, never()).findByEventId(any());
            verify(bookingRepository, never()).countByTicketCategoryId(any());
        }
    }
}
