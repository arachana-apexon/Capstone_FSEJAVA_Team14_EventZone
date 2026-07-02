package com.eventzone.eventzone_backend.service;

import com.eventzone.eventzone_backend.dto.BookingSummaryResponse;
import com.eventzone.eventzone_backend.dto.TicketCategoryRequest;
import com.eventzone.eventzone_backend.dto.TicketCategoryResponse;
import com.eventzone.eventzone_backend.entity.Event;
import com.eventzone.eventzone_backend.entity.TicketCategory;
import com.eventzone.eventzone_backend.repository.BookingRepository;
import com.eventzone.eventzone_backend.repository.EventRepository;
import com.eventzone.eventzone_backend.repository.TicketCategoryRepository;
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
public class TicketCategoryService {
    
    private final TicketCategoryRepository ticketCategoryRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    
    @Transactional
    public TicketCategoryResponse createTicketCategory(TicketCategoryRequest request) {
        Objects.requireNonNull(request, "Ticket category request cannot be null");
        
        String name = Objects.requireNonNull(request.getName(), "Name cannot be null");
        BigDecimal price = Objects.requireNonNull(request.getPrice(), "Price cannot be null");
        Integer totalSeats = Objects.requireNonNull(request.getTotalSeats(), "Total seats cannot be null");
        
        if (totalSeats <= 0) {
            throw new RuntimeException("Total seats must be greater than 0");
        }
        
        Event event = eventRepository.findById(Objects.requireNonNull(request.getEventId(), "Event ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + request.getEventId()));
        
        TicketCategory ticketCategory = TicketCategory.builder()
                .name(name)
                .price(price)
                .totalSeats(totalSeats)
                .availableSeats(totalSeats)
                .event(event)
                .build();
        
        @SuppressWarnings("null")
        TicketCategory savedTicketCategory = Objects.requireNonNull(ticketCategoryRepository.save(ticketCategory), "Failed to save ticket category");
        return mapToResponse(savedTicketCategory);
    }
    
    @Transactional(readOnly = true)
    public List<TicketCategoryResponse> getTicketCategoriesByEvent(UUID eventId) {
        Objects.requireNonNull(eventId, "Event ID cannot be null");
        
        List<TicketCategory> ticketCategories = ticketCategoryRepository.findByEventId(eventId);
        return ticketCategories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public TicketCategoryResponse updateTicketCategory(UUID id, TicketCategoryRequest request) {
        Objects.requireNonNull(request, "Ticket category request cannot be null");
        
        String name = Objects.requireNonNull(request.getName(), "Name cannot be null");
        BigDecimal price = Objects.requireNonNull(request.getPrice(), "Price cannot be null");
        Integer totalSeats = Objects.requireNonNull(request.getTotalSeats(), "Total seats cannot be null");
        
        if (totalSeats <= 0) {
            throw new RuntimeException("Total seats must be greater than 0");
        }
        
        TicketCategory ticketCategory = ticketCategoryRepository.findById(Objects.requireNonNull(id, "Ticket category ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Ticket category not found with id: " + id));
        
        ticketCategory.setName(name);
        ticketCategory.setPrice(price);
        ticketCategory.setTotalSeats(totalSeats);
        
        TicketCategory updatedTicketCategory = ticketCategoryRepository.save(ticketCategory);
        return mapToResponse(updatedTicketCategory);
    }
    
    @Transactional
    public void deleteTicketCategory(UUID id) {
        ticketCategoryRepository.deleteById(Objects.requireNonNull(id, "Ticket category ID cannot be null"));
    }
    
    @Transactional(readOnly = true)
    public List<BookingSummaryResponse> getBookingSummaryByEvent(UUID eventId) {
        Objects.requireNonNull(eventId, "Event ID cannot be null");
        
        List<TicketCategory> ticketCategories = ticketCategoryRepository.findByEventId(eventId);
        
        return ticketCategories.stream()
                .map(ticketCategory -> {
                    Objects.requireNonNull(ticketCategory, "Ticket category cannot be null");
                    UUID ticketCategoryId = Objects.requireNonNull(ticketCategory.getId(), "Ticket category ID cannot be null");
                    
                    long bookedSeatsCount = bookingRepository.countByTicketCategoryId(ticketCategoryId);
                    int bookedSeats = (int) bookedSeatsCount;
                    int totalSeats = Objects.requireNonNull(ticketCategory.getTotalSeats(), "Total seats cannot be null");
                    int availableSeats = Objects.requireNonNull(ticketCategory.getAvailableSeats(), "Available seats cannot be null");
                    
                    return BookingSummaryResponse.builder()
                            .ticketCategoryId(ticketCategoryId)
                            .ticketCategoryName(ticketCategory.getName())
                            .totalSeats(totalSeats)
                            .availableSeats(availableSeats)
                            .bookedSeats(bookedSeats)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private TicketCategoryResponse mapToResponse(TicketCategory ticketCategory) {
        Objects.requireNonNull(ticketCategory, "Ticket category cannot be null");
        
        Event event = Objects.requireNonNull(ticketCategory.getEvent(), "Event cannot be null");
        
        return TicketCategoryResponse.builder()
                .id(ticketCategory.getId())
                .name(ticketCategory.getName())
                .price(ticketCategory.getPrice())
                .totalSeats(ticketCategory.getTotalSeats())
                .availableSeats(ticketCategory.getAvailableSeats())
                .eventTitle(event.getTitle())
                .build();
    }
}
