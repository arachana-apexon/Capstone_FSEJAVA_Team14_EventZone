package com.eventzone.eventzone_backend.service;

import com.eventzone.eventzone_backend.constants.Roles;
import com.eventzone.eventzone_backend.dto.CategoryRequest;
import com.eventzone.eventzone_backend.dto.CategoryResponse;
import com.eventzone.eventzone_backend.entity.EventCategory;
import com.eventzone.eventzone_backend.entity.User;
import com.eventzone.eventzone_backend.repository.EventCategoryRepository;
import com.eventzone.eventzone_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final EventCategoryRepository eventCategoryRepository;
    private final UserRepository userRepository;
    
    /**
     * Verify that the user has ADMIN role.
     * Throws RuntimeException if user does not have ADMIN role.
     * 
     * @param user the user to verify
     * @throws RuntimeException if user role is not ADMIN
     */
    private void verifyAdminRole(User user) {
        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }
        
        String userRole = user.getRole();
        if (userRole == null) {
            throw new RuntimeException("User role cannot be null");
        }
        
        // Check if user is ADMIN
        if (!Roles.ADMIN.equals(userRole)) {
            throw new RuntimeException("Access denied");
        }
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        Objects.requireNonNull(request, "Category request cannot be null");
        
        String name = Objects.requireNonNull(request.getName(), "Category name cannot be null");
        
        // Find user by userId
        User user = userRepository.findById(Objects.requireNonNull(request.getUserId(), "User ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify user has ADMIN role
        verifyAdminRole(user);
        
        // Check if category already exists
        if (eventCategoryRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Category already exists");
        }

        // Create and save category
        EventCategory category = EventCategory.builder()
                .name(name)
                .build();

        @SuppressWarnings("null")
        EventCategory savedCategory = Objects.requireNonNull(eventCategoryRepository.save(category), "Failed to save category");

        // Return response
        return new CategoryResponse(
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getActive()
        );
    }

    public List<CategoryResponse> getAllCategories() {
        return eventCategoryRepository.findAll().stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getActive()
                ))
                .collect(Collectors.toList());
    }

    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Objects.requireNonNull(request, "Category request cannot be null");
        
        String name = Objects.requireNonNull(request.getName(), "Category name cannot be null");
        
        // Find user by userId
        User user = userRepository.findById(Objects.requireNonNull(request.getUserId(), "User ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify user has ADMIN role
        verifyAdminRole(user);
        
        // Find category by id
        EventCategory category = eventCategoryRepository.findById(Objects.requireNonNull(id, "Category ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Update name
        category.setName(name);
        // Save and return response
        EventCategory updatedCategory = eventCategoryRepository.save(category);

        return new CategoryResponse(
                updatedCategory.getId(),
                updatedCategory.getName(),
                updatedCategory.getActive()
        );
    }

    public void deleteCategory(UUID id, UUID userId) {
        // Find user by userId
        User user = userRepository.findById(Objects.requireNonNull(userId, "User ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify user has ADMIN role
        verifyAdminRole(user);
        
        // Find category by id
        EventCategory category = eventCategoryRepository.findById(Objects.requireNonNull(id, "Category ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Soft delete - set active to false
        category.setActive(false);

        // Save
        eventCategoryRepository.save(category);
    }
}
