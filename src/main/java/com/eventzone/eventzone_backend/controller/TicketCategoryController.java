package com.eventzone.eventzone_backend.controller;

import com.eventzone.eventzone_backend.dto.BookingSummaryResponse;
import com.eventzone.eventzone_backend.dto.TicketCategoryRequest;
import com.eventzone.eventzone_backend.dto.TicketCategoryResponse;
import com.eventzone.eventzone_backend.service.TicketCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ticket-categories")
@RequiredArgsConstructor
public class TicketCategoryController {
    
    private final TicketCategoryService ticketCategoryService;
    
    @PostMapping
    public ResponseEntity<TicketCategoryResponse> createTicketCategory(
            @Valid @RequestBody TicketCategoryRequest request) {
        TicketCategoryResponse response = ticketCategoryService.createTicketCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<TicketCategoryResponse>> getTicketCategoriesByEvent(
            @PathVariable UUID eventId) {
        List<TicketCategoryResponse> responses = ticketCategoryService.getTicketCategoriesByEvent(eventId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/event/{eventId}/summary")
    public ResponseEntity<List<BookingSummaryResponse>> getBookingSummaryByEvent(
            @PathVariable UUID eventId) {
        List<BookingSummaryResponse> responses = ticketCategoryService.getBookingSummaryByEvent(eventId);
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TicketCategoryResponse> updateTicketCategory(
            @PathVariable UUID id,
            @Valid @RequestBody TicketCategoryRequest request) {
        TicketCategoryResponse response = ticketCategoryService.updateTicketCategory(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketCategory(@PathVariable UUID id) {
        ticketCategoryService.deleteTicketCategory(id);
        return ResponseEntity.noContent().build();
    }
}
