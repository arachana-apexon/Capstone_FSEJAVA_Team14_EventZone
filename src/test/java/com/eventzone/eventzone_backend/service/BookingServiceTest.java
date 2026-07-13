package com.eventzone.eventzone_backend.service;

import com.eventzone.eventzone_backend.dto.BookingRequest;
import com.eventzone.eventzone_backend.dto.BookingResponse;
import com.eventzone.eventzone_backend.entity.Booking;
import com.eventzone.eventzone_backend.entity.Event;
import com.eventzone.eventzone_backend.entity.TicketCategory;
import com.eventzone.eventzone_backend.entity.User;
import com.eventzone.eventzone_backend.repository.BookingRepository;
import com.eventzone.eventzone_backend.repository.TicketCategoryRepository;
import com.eventzone.eventzone_backend.repository.UserRepository;
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
@DisplayName("BookingService Tests")
@SuppressWarnings("null")
class BookingServiceTest {
    
    @Mock
    private BookingRepository bookingRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private TicketCategoryRepository ticketCategoryRepository;
    
    @InjectMocks
    private BookingService bookingService;
    
    private UUID userId;
    private UUID ticketCategoryId;
    private UUID eventId;
    private UUID bookingId;
    private User user;
    private Event event;
    private TicketCategory ticketCategory;
    private Booking booking;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        ticketCategoryId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        bookingId = UUID.randomUUID();
        
        user = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john@example.com")
                .role("ATTENDEE")
                .build();
        
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
                .availableSeats(50)
                .event(event)
                .build();
        
        booking = Booking.builder()
                .id(bookingId)
                .bookingRef("BOOK-12345678")
                .quantity(2)
                .totalAmount(new BigDecimal("200.00"))
                .status("CONFIRMED")
                .user(user)
                .ticketCategory(ticketCategory)
                .build();
    }
    
    @Nested
    @DisplayName("Create Booking Tests")
    class CreateBookingTests {
        
        @Test
        @DisplayName("Should successfully create booking")
        void createBooking_shouldSuccessfullyCreateBooking() {
            // Arrange
            BookingRequest request = BookingRequest.builder()
                    .userId(userId)
                    .ticketCategoryId(ticketCategoryId)
                    .quantity(2)
                    .build();
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(ticketCategoryRepository.findById(ticketCategoryId)).thenReturn(Optional.of(ticketCategory));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
            when(ticketCategoryRepository.save(any(TicketCategory.class))).thenReturn(ticketCategory);
            
            // Act
            BookingResponse response = bookingService.createBooking(request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals("BOOK-12345678", response.getBookingRef(), "Booking reference should match");
            assertEquals(2, response.getQuantity(), "Quantity should match");
            assertEquals("CONFIRMED", response.getStatus(), "Status should be CONFIRMED");
            assertEquals("Tech Conference 2026", response.getEventTitle(), "Event title should match");
            assertEquals("VIP", response.getTicketCategoryName(), "Ticket category name should match");
            assertEquals(new BigDecimal("100.00"), response.getUnitPrice(), "Unit price should match ticket price");
            assertEquals(new BigDecimal("200.00"), response.getTotalAmount(), "Total amount should be price times quantity");
            assertEquals(48, ticketCategory.getAvailableSeats(), "Available seats should be reduced by 2");
            
            verify(userRepository, times(1)).findById(userId);
            verify(ticketCategoryRepository, times(1)).findById(ticketCategoryId);
            verify(bookingRepository, times(1)).save(any(Booking.class));
            verify(ticketCategoryRepository, times(1)).save(ticketCategory);
        }
        
        @Test
        @DisplayName("Should throw exception when user not found")
        void createBooking_shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            UUID nonExistentUserId = UUID.randomUUID();
            BookingRequest request = BookingRequest.builder()
                    .userId(nonExistentUserId)
                    .ticketCategoryId(ticketCategoryId)
                    .quantity(2)
                    .build();
            
            when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bookingService.createBooking(request);
            }, "Should throw RuntimeException when user not found");
            
            assertEquals("User not found", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(nonExistentUserId);
            verify(ticketCategoryRepository, never()).findById(any());
            verify(bookingRepository, never()).save(any(Booking.class));
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when ticket category not found")
        void createBooking_shouldThrowExceptionWhenTicketCategoryNotFound() {
            // Arrange
            UUID nonExistentTicketCategoryId = UUID.randomUUID();
            BookingRequest request = BookingRequest.builder()
                    .userId(userId)
                    .ticketCategoryId(nonExistentTicketCategoryId)
                    .quantity(2)
                    .build();
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(ticketCategoryRepository.findById(nonExistentTicketCategoryId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bookingService.createBooking(request);
            }, "Should throw RuntimeException when ticket category not found");
            
            assertEquals("Ticket category not found", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(userId);
            verify(ticketCategoryRepository, times(1)).findById(nonExistentTicketCategoryId);
            verify(bookingRepository, never()).save(any(Booking.class));
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when not enough seats available")
        void createBooking_shouldThrowExceptionWhenNotEnoughSeats() {
            // Arrange
            BookingRequest request = BookingRequest.builder()
                    .userId(userId)
                    .ticketCategoryId(ticketCategoryId)
                    .quantity(60)
                    .build();
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(ticketCategoryRepository.findById(ticketCategoryId)).thenReturn(Optional.of(ticketCategory));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bookingService.createBooking(request);
            }, "Should throw RuntimeException when not enough seats available");
            
            assertEquals("Not enough seats available", exception.getMessage(), "Exception message should match");
            assertEquals(50, ticketCategory.getAvailableSeats(), "Available seats should not change");
            
            verify(userRepository, times(1)).findById(userId);
            verify(ticketCategoryRepository, times(1)).findById(ticketCategoryId);
            verify(bookingRepository, never()).save(any(Booking.class));
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when exactly at seat limit")
        void createBooking_shouldThrowExceptionWhenExactlyAtSeatLimit() {
            // Arrange
            BookingRequest request = BookingRequest.builder()
                    .userId(userId)
                    .ticketCategoryId(ticketCategoryId)
                    .quantity(51)
                    .build();
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(ticketCategoryRepository.findById(ticketCategoryId)).thenReturn(Optional.of(ticketCategory));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bookingService.createBooking(request);
            }, "Should throw RuntimeException when quantity exceeds available seats by 1");
            
            assertEquals("Not enough seats available", exception.getMessage(), "Exception message should match");
            assertEquals(50, ticketCategory.getAvailableSeats(), "Available seats should not change");
            
            verify(userRepository, times(1)).findById(userId);
            verify(ticketCategoryRepository, times(1)).findById(ticketCategoryId);
            verify(bookingRepository, never()).save(any(Booking.class));
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when request is null")
        void createBooking_shouldThrowExceptionWhenRequestIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                bookingService.createBooking(null);
            }, "Should throw NullPointerException when request is null");
            
            assertEquals("Booking request cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).findById(any());
            verify(bookingRepository, never()).save(any(Booking.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when userId is null")
        void createBooking_shouldThrowExceptionWhenUserIdIsNull() {
            // Arrange
            BookingRequest request = BookingRequest.builder()
                    .userId(null)
                    .ticketCategoryId(ticketCategoryId)
                    .quantity(2)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                bookingService.createBooking(request);
            }, "Should throw NullPointerException when userId is null");
            
            assertEquals("User ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).findById(any());
            verify(bookingRepository, never()).save(any(Booking.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when ticketCategoryId is null")
        void createBooking_shouldThrowExceptionWhenTicketCategoryIdIsNull() {
            // Arrange
            BookingRequest request = BookingRequest.builder()
                    .userId(userId)
                    .ticketCategoryId(null)
                    .quantity(2)
                    .build();
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                bookingService.createBooking(request);
            }, "Should throw NullPointerException when ticketCategoryId is null");
            
            assertEquals("Ticket category ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(userId);
            verify(ticketCategoryRepository, never()).findById(any());
            verify(bookingRepository, never()).save(any(Booking.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when quantity is null")
        void createBooking_shouldThrowExceptionWhenQuantityIsNull() {
            // Arrange
            BookingRequest request = BookingRequest.builder()
                    .userId(userId)
                    .ticketCategoryId(ticketCategoryId)
                    .quantity(null)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                bookingService.createBooking(request);
            }, "Should throw NullPointerException when quantity is null");
            
            assertEquals("Quantity cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).findById(any());
            verify(bookingRepository, never()).save(any(Booking.class));
        }
        
        @Test
        @DisplayName("Should throw exception when quantity is zero")
        void createBooking_shouldThrowExceptionWhenQuantityIsZero() {
            // Arrange
            BookingRequest request = BookingRequest.builder()
                    .userId(userId)
                    .ticketCategoryId(ticketCategoryId)
                    .quantity(0)
                    .build();
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bookingService.createBooking(request);
            }, "Should throw RuntimeException when quantity is zero");
            
            assertEquals("Quantity must be greater than 0", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).findById(any());
            verify(bookingRepository, never()).save(any(Booking.class));
        }
        
        @Test
        @DisplayName("Should throw exception when quantity is negative")
        void createBooking_shouldThrowExceptionWhenQuantityIsNegative() {
            // Arrange
            BookingRequest request = BookingRequest.builder()
                    .userId(userId)
                    .ticketCategoryId(ticketCategoryId)
                    .quantity(-5)
                    .build();
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bookingService.createBooking(request);
            }, "Should throw RuntimeException when quantity is negative");
            
            assertEquals("Quantity must be greater than 0", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(ticketCategoryRepository, never()).findById(any());
            verify(bookingRepository, never()).save(any(Booking.class));
        }
    }
    
    @Nested
    @DisplayName("Get Bookings By User Tests")
    class GetBookingsByUserTests {
        
        @Test
        @DisplayName("Should successfully return all bookings for user")
        void getBookingsByUser_shouldReturnAllBookingsForUser() {
            // Arrange
            TicketCategory ticketCategory2 = TicketCategory.builder()
                    .id(UUID.randomUUID())
                    .name("General")
                    .price(new BigDecimal("50.00"))
                    .totalSeats(200)
                    .availableSeats(150)
                    .event(event)
                    .build();
            
            Booking booking1 = Booking.builder()
                    .id(UUID.randomUUID())
                    .bookingRef("BOOK-12345678")
                    .quantity(2)
                    .status("CONFIRMED")
                    .user(user)
                    .ticketCategory(ticketCategory)
                    .build();
            
            Booking booking2 = Booking.builder()
                    .id(UUID.randomUUID())
                    .bookingRef("BOOK-87654321")
                    .quantity(3)
                    .status("CONFIRMED")
                    .user(user)
                    .ticketCategory(ticketCategory2)
                    .build();
            
            List<Booking> bookings = Arrays.asList(booking1, booking2);
            
            when(bookingRepository.findByUserId(userId)).thenReturn(bookings);
            
            // Act
            List<BookingResponse> responses = bookingService.getBookingsByUser(userId);
            
            // Assert
            assertNotNull(responses, "Response list should not be null");
            assertEquals(2, responses.size(), "Should return 2 bookings");
            
            assertEquals("BOOK-12345678", responses.get(0).getBookingRef(), "First booking reference should match");
            assertEquals(2, responses.get(0).getQuantity(), "First booking quantity should match");
            assertEquals("VIP", responses.get(0).getTicketCategoryName(), "First booking ticket category should match");
            assertEquals("CONFIRMED", responses.get(0).getStatus(), "First booking status should match");
            assertEquals("Tech Conference 2026", responses.get(0).getEventTitle(), "First booking event title should match");
            
            assertEquals("BOOK-87654321", responses.get(1).getBookingRef(), "Second booking reference should match");
            assertEquals(3, responses.get(1).getQuantity(), "Second booking quantity should match");
            assertEquals("General", responses.get(1).getTicketCategoryName(), "Second booking ticket category should match");
            assertEquals("CONFIRMED", responses.get(1).getStatus(), "Second booking status should match");
            
            verify(bookingRepository, times(1)).findByUserId(userId);
        }
        
        @Test
        @DisplayName("Should return empty list when user has no bookings")
        void getBookingsByUser_shouldReturnEmptyListWhenUserHasNoBookings() {
            // Arrange
            when(bookingRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
            
            // Act
            List<BookingResponse> responses = bookingService.getBookingsByUser(userId);
            
            // Assert
            assertNotNull(responses, "Response list should not be null");
            assertEquals(0, responses.size(), "Should return empty list");
            
            verify(bookingRepository, times(1)).findByUserId(userId);
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when userId is null")
        void getBookingsByUser_shouldThrowExceptionWhenUserIdIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                bookingService.getBookingsByUser(null);
            }, "Should throw NullPointerException when userId is null");
            
            assertEquals("User ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(bookingRepository, never()).findByUserId(any());
        }
    }
    
    @Nested
    @DisplayName("Cancel Booking Tests")
    class CancelBookingTests {
        
        @Test
        @DisplayName("Should successfully cancel booking and restore seats")
        void cancelBooking_shouldSuccessfullyCancelBookingAndRestoreSeats() {
            // Arrange
            // Simulate that 2 seats were already booked (48 remaining out of 50)
            ticketCategory.setAvailableSeats(48);
            
            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
            when(ticketCategoryRepository.save(any(TicketCategory.class))).thenReturn(ticketCategory);
            
            // Act
            BookingResponse response = bookingService.cancelBooking(bookingId);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals("CANCELLED", response.getStatus(), "Status should be CANCELLED");
            assertEquals("BOOK-12345678", response.getBookingRef(), "Booking reference should match");
            assertEquals(2, response.getQuantity(), "Quantity should match");
            assertEquals("Tech Conference 2026", response.getEventTitle(), "Event title should match");
            assertEquals("VIP", response.getTicketCategoryName(), "Ticket category name should match");
            
            assertEquals(50, ticketCategory.getAvailableSeats(), "Available seats should be restored by 2");
            assertEquals("CANCELLED", booking.getStatus(), "Booking status should be CANCELLED");
            
            verify(bookingRepository, times(1)).findById(bookingId);
            verify(bookingRepository, times(1)).save(booking);
            verify(ticketCategoryRepository, times(1)).save(ticketCategory);
        }
        
        @Test
        @DisplayName("Should throw exception when booking not found")
        void cancelBooking_shouldThrowExceptionWhenBookingNotFound() {
            // Arrange
            UUID nonExistentBookingId = UUID.randomUUID();
            when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bookingService.cancelBooking(nonExistentBookingId);
            }, "Should throw RuntimeException when booking not found");
            
            assertEquals("Booking not found", exception.getMessage(), "Exception message should match");
            
            verify(bookingRepository, times(1)).findById(nonExistentBookingId);
            verify(bookingRepository, never()).save(any(Booking.class));
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when booking is already cancelled")
        void cancelBooking_shouldThrowExceptionWhenBookingIsAlreadyCancelled() {
            // Arrange
            booking.setStatus("CANCELLED");
            ticketCategory.setAvailableSeats(50);
            
            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bookingService.cancelBooking(bookingId);
            }, "Should throw RuntimeException when booking is already cancelled");
            
            assertEquals("Booking is already cancelled", exception.getMessage(), "Exception message should match");
            assertEquals(50, ticketCategory.getAvailableSeats(), "Available seats should not change");
            
            verify(bookingRepository, times(1)).findById(bookingId);
            verify(bookingRepository, never()).save(any(Booking.class));
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when bookingId is null")
        void cancelBooking_shouldThrowExceptionWhenBookingIdIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                bookingService.cancelBooking(null);
            }, "Should throw NullPointerException when bookingId is null");
            
            assertEquals("Booking ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(bookingRepository, never()).findById(any());
            verify(bookingRepository, never()).save(any(Booking.class));
            verify(ticketCategoryRepository, never()).save(any(TicketCategory.class));
        }
    }
}
