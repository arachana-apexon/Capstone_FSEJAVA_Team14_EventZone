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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Objects.requireNonNull(request, "Booking request cannot be null");
        
        Integer quantity = Objects.requireNonNull(request.getQuantity(), "Quantity cannot be null");
        
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
        
        // Find user
        User user = userRepository.findById(Objects.requireNonNull(request.getUserId(), "User ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Find ticket category
        TicketCategory ticketCategory = ticketCategoryRepository.findById(Objects.requireNonNull(request.getTicketCategoryId(), "Ticket category ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Ticket category not found"));
        
        Integer availableSeats = Objects.requireNonNull(ticketCategory.getAvailableSeats(), "Available seats cannot be null");
        
        // Check available seats
        if (availableSeats < quantity) {
            throw new RuntimeException("Not enough seats available");
        }
        
        // Reduce available seats
        ticketCategory.setAvailableSeats(availableSeats - quantity);
        
        // Calculate total amount = unit price * quantity
        BigDecimal unitPrice = Objects.requireNonNull(ticketCategory.getPrice(), "Ticket price cannot be null");
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        
        // Generate booking reference: BOOK- + first 8 chars of UUID
        String bookingRef = "BOOK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create booking
        Booking booking = Booking.builder()
                .bookingRef(bookingRef)
                .quantity(quantity)
                .totalAmount(totalAmount)
                .user(user)
                .ticketCategory(ticketCategory)
                .build();
        
        // Save booking
        @SuppressWarnings("null")
        Booking savedBooking = Objects.requireNonNull(bookingRepository.save(booking), "Failed to save booking");
        
        // Save ticket category
        ticketCategoryRepository.save(ticketCategory);
        
        // Return BookingResponse
        return mapToResponse(savedBooking);
    }
    
    public List<BookingResponse> getBookingsByUser(UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        
        // Return all bookings for a user
        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public BookingResponse cancelBooking(UUID bookingId) {
        // Find booking
        Booking booking = bookingRepository.findById(Objects.requireNonNull(bookingId, "Booking ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // If already cancelled throw RuntimeException
        String bookingStatus = Objects.requireNonNull(booking.getStatus(), "Booking status cannot be null");
        if ("CANCELLED".equals(bookingStatus)) {
            throw new RuntimeException("Booking is already cancelled");
        }
        
        // Restore seats
        TicketCategory ticketCategory = Objects.requireNonNull(booking.getTicketCategory(), "Ticket category cannot be null");
        Integer currentAvailableSeats = Objects.requireNonNull(ticketCategory.getAvailableSeats(), "Available seats cannot be null");
        Integer bookingQuantity = Objects.requireNonNull(booking.getQuantity(), "Booking quantity cannot be null");
        ticketCategory.setAvailableSeats(currentAvailableSeats + bookingQuantity);
        
        // Set status to CANCELLED
        booking.setStatus("CANCELLED");
        
        // Save booking
        bookingRepository.save(booking);
        
        // Save ticket category
        ticketCategoryRepository.save(ticketCategory);
        
        // Return BookingResponse
        return mapToResponse(booking);
    }
    
    // Private helper method to map Booking to BookingResponse
    private BookingResponse mapToResponse(Booking booking) {
        Objects.requireNonNull(booking, "Booking cannot be null");
        
        TicketCategory ticketCategory = Objects.requireNonNull(booking.getTicketCategory(), "Ticket category cannot be null");
        Event event = Objects.requireNonNull(ticketCategory.getEvent(), "Event cannot be null");
        
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .bookingRef(booking.getBookingRef())
                .eventTitle(event.getTitle())
                .ticketCategoryName(ticketCategory.getName())
                .quantity(booking.getQuantity())
                .unitPrice(ticketCategory.getPrice())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .build();
    }
    
}
