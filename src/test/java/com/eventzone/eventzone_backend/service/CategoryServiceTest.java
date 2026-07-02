package com.eventzone.eventzone_backend.service;

import com.eventzone.eventzone_backend.dto.CategoryRequest;
import com.eventzone.eventzone_backend.dto.CategoryResponse;
import com.eventzone.eventzone_backend.entity.EventCategory;
import com.eventzone.eventzone_backend.entity.User;
import com.eventzone.eventzone_backend.repository.EventCategoryRepository;
import com.eventzone.eventzone_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
@SuppressWarnings("null")
class CategoryServiceTest {
    
    @Mock
    private EventCategoryRepository eventCategoryRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private CategoryService categoryService;
    
    private UUID adminUserId;
    private UUID attendeeUserId;
    private UUID organiserUserId;
    private UUID categoryId;
    private User adminUser;
    private User attendeeUser;
    private User organiserUser;
    
    @BeforeEach
    void setUp() {
        adminUserId = UUID.randomUUID();
        attendeeUserId = UUID.randomUUID();
        organiserUserId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        
        adminUser = User.builder()
                .id(adminUserId)
                .name("Admin User")
                .email("admin@example.com")
                .role("ADMIN")
                .build();
        
        attendeeUser = User.builder()
                .id(attendeeUserId)
                .name("Attendee User")
                .email("attendee@example.com")
                .role("ATTENDEE")
                .build();
        
        organiserUser = User.builder()
                .id(organiserUserId)
                .name("Organiser User")
                .email("organiser@example.com")
                .role("ORGANISER")
                .build();
    }
    
    @Nested
    @DisplayName("Create Category Tests")
    class CreateCategoryTests {
        
        @Test
        @DisplayName("Should successfully create category when user is ADMIN")
        void createCategory_shouldSuccessWithAdminUser() {
            // Arrange
            CategoryRequest request = new CategoryRequest("Music", adminUserId);
            
            EventCategory savedCategory = EventCategory.builder()
                    .id(categoryId)
                    .name("Music")
                    .active(true)
                    .build();
            
            when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
            when(eventCategoryRepository.findByName("Music")).thenReturn(Optional.empty());
            when(eventCategoryRepository.save(any(EventCategory.class))).thenReturn(savedCategory);
            
            // Act
            CategoryResponse response = categoryService.createCategory(request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(categoryId, response.getId(), "Category ID should match");
            assertEquals("Music", response.getName(), "Category name should match");
            assertEquals(true, response.getActive(), "Category should be active");
            
            verify(userRepository, times(1)).findById(adminUserId);
            verify(eventCategoryRepository, times(1)).findByName("Music");
            verify(eventCategoryRepository, times(1)).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when category name already exists")
        void createCategory_shouldThrowExceptionWhenDuplicateName() {
            // Arrange
            CategoryRequest request = new CategoryRequest("Music", adminUserId);
            
            EventCategory existingCategory = EventCategory.builder()
                    .id(UUID.randomUUID())
                    .name("Music")
                    .active(true)
                    .build();
            
            when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
            when(eventCategoryRepository.findByName("Music")).thenReturn(Optional.of(existingCategory));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.createCategory(request);
            }, "Should throw RuntimeException when category name already exists");
            
            assertEquals("Category already exists", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(adminUserId);
            verify(eventCategoryRepository, times(1)).findByName("Music");
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when user is ATTENDEE - Access Denied")
        void createCategory_shouldThrowExceptionWhenUserIsAttendee() {
            // Arrange
            CategoryRequest request = new CategoryRequest("Music", attendeeUserId);
            
            when(userRepository.findById(attendeeUserId)).thenReturn(Optional.of(attendeeUser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.createCategory(request);
            }, "Should throw RuntimeException when user is ATTENDEE");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(attendeeUserId);
            verify(eventCategoryRepository, never()).findByName(anyString());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when user is ORGANISER - Access Denied")
        void createCategory_shouldThrowExceptionWhenUserIsOrganiser() {
            // Arrange
            CategoryRequest request = new CategoryRequest("Music", organiserUserId);
            
            when(userRepository.findById(organiserUserId)).thenReturn(Optional.of(organiserUser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.createCategory(request);
            }, "Should throw RuntimeException when user is ORGANISER");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(organiserUserId);
            verify(eventCategoryRepository, never()).findByName(anyString());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when user not found")
        void createCategory_shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            UUID nonExistentUserId = UUID.randomUUID();
            CategoryRequest request = new CategoryRequest("Music", nonExistentUserId);
            
            when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.createCategory(request);
            }, "Should throw RuntimeException when user not found");
            
            assertEquals("User not found", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(nonExistentUserId);
            verify(eventCategoryRepository, never()).findByName(anyString());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when request is null")
        void createCategory_shouldThrowExceptionWhenRequestIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                categoryService.createCategory(null);
            }, "Should throw NullPointerException when request is null");
            
            assertEquals("Category request cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).findByName(anyString());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when category name is null")
        void createCategory_shouldThrowExceptionWhenNameIsNull() {
            // Arrange
            CategoryRequest request = new CategoryRequest(null, adminUserId);
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                categoryService.createCategory(request);
            }, "Should throw NullPointerException when name is null");
            
            assertEquals("Category name cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).findByName(anyString());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when userId is null")
        void createCategory_shouldThrowExceptionWhenUserIdIsNull() {
            // Arrange
            CategoryRequest request = new CategoryRequest("Music", null);
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                categoryService.createCategory(request);
            }, "Should throw NullPointerException when userId is null");
            
            assertEquals("User ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).findByName(anyString());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when user role is null")
        void createCategory_shouldThrowExceptionWhenUserRoleIsNull() {
            // Arrange
            User userWithNullRole = User.builder()
                    .id(adminUserId)
                    .name("Admin User")
                    .email("admin@example.com")
                    .role(null)
                    .build();
            
            CategoryRequest request = new CategoryRequest("Music", adminUserId);
            
            when(userRepository.findById(adminUserId)).thenReturn(Optional.of(userWithNullRole));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.createCategory(request);
            }, "Should throw RuntimeException when user role is null");
            
            assertEquals("User role cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(adminUserId);
            verify(eventCategoryRepository, never()).findByName(anyString());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
    }
    
    @Nested
    @DisplayName("Get All Categories Tests")
    class GetAllCategoriesTests {
        
        @Test
        @DisplayName("Should successfully return all categories")
        void getAllCategories_shouldReturnAllCategories() {
            // Arrange
            EventCategory category1 = EventCategory.builder()
                    .id(UUID.randomUUID())
                    .name("Music")
                    .active(true)
                    .build();
            
            EventCategory category2 = EventCategory.builder()
                    .id(UUID.randomUUID())
                    .name("Sports")
                    .active(true)
                    .build();
            
            EventCategory category3 = EventCategory.builder()
                    .id(UUID.randomUUID())
                    .name("Technology")
                    .active(false)
                    .build();
            
            List<EventCategory> categories = Arrays.asList(category1, category2, category3);
            
            when(eventCategoryRepository.findAll()).thenReturn(categories);
            
            // Act
            List<CategoryResponse> responses = categoryService.getAllCategories();
            
            // Assert
            assertNotNull(responses, "Response list should not be null");
            assertEquals(3, responses.size(), "Should return 3 categories");
            assertEquals("Music", responses.get(0).getName(), "First category name should match");
            assertEquals("Sports", responses.get(1).getName(), "Second category name should match");
            assertEquals("Technology", responses.get(2).getName(), "Third category name should match");
            assertEquals(true, responses.get(0).getActive(), "First category should be active");
            assertEquals(false, responses.get(2).getActive(), "Third category should be inactive");
            
            verify(eventCategoryRepository, times(1)).findAll();
        }
        
        @Test
        @DisplayName("Should return empty list when no categories exist")
        void getAllCategories_shouldReturnEmptyListWhenNoCategoriesExist() {
            // Arrange
            when(eventCategoryRepository.findAll()).thenReturn(Collections.emptyList());
            
            // Act
            List<CategoryResponse> responses = categoryService.getAllCategories();
            
            // Assert
            assertNotNull(responses, "Response list should not be null");
            assertEquals(0, responses.size(), "Should return empty list");
            
            verify(eventCategoryRepository, times(1)).findAll();
        }
    }
    
    @Nested
    @DisplayName("Update Category Tests")
    class UpdateCategoryTests {
        
        @Test
        @DisplayName("Should successfully update category when user is ADMIN")
        void updateCategory_shouldSuccessWithAdminUser() {
            // Arrange
            CategoryRequest request = new CategoryRequest("Updated Music", adminUserId);
            
            EventCategory existingCategory = EventCategory.builder()
                    .id(categoryId)
                    .name("Music")
                    .active(true)
                    .build();
            
            EventCategory updatedCategory = EventCategory.builder()
                    .id(categoryId)
                    .name("Updated Music")
                    .active(true)
                    .build();
            
            when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
            when(eventCategoryRepository.save(any(EventCategory.class))).thenReturn(updatedCategory);
            
            // Act
            CategoryResponse response = categoryService.updateCategory(categoryId, request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(categoryId, response.getId(), "Category ID should match");
            assertEquals("Updated Music", response.getName(), "Category name should be updated");
            assertEquals(true, response.getActive(), "Category should be active");
            
            verify(userRepository, times(1)).findById(adminUserId);
            verify(eventCategoryRepository, times(1)).findById(categoryId);
            verify(eventCategoryRepository, times(1)).save(existingCategory);
        }
        
        @Test
        @DisplayName("Should throw exception when category not found")
        void updateCategory_shouldThrowExceptionWhenCategoryNotFound() {
            // Arrange
            UUID nonExistentCategoryId = UUID.randomUUID();
            CategoryRequest request = new CategoryRequest("Updated Music", adminUserId);
            
            when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
            when(eventCategoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.updateCategory(nonExistentCategoryId, request);
            }, "Should throw RuntimeException when category not found");
            
            assertEquals("Category not found", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(adminUserId);
            verify(eventCategoryRepository, times(1)).findById(nonExistentCategoryId);
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when user is ATTENDEE - Access Denied")
        void updateCategory_shouldThrowExceptionWhenUserIsAttendee() {
            // Arrange
            CategoryRequest request = new CategoryRequest("Updated Music", attendeeUserId);
            
            when(userRepository.findById(attendeeUserId)).thenReturn(Optional.of(attendeeUser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.updateCategory(categoryId, request);
            }, "Should throw RuntimeException when user is ATTENDEE");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(attendeeUserId);
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when user is ORGANISER - Access Denied")
        void updateCategory_shouldThrowExceptionWhenUserIsOrganiser() {
            // Arrange
            CategoryRequest request = new CategoryRequest("Updated Music", organiserUserId);
            
            when(userRepository.findById(organiserUserId)).thenReturn(Optional.of(organiserUser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.updateCategory(categoryId, request);
            }, "Should throw RuntimeException when user is ORGANISER");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(organiserUserId);
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when user not found")
        void updateCategory_shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            UUID nonExistentUserId = UUID.randomUUID();
            CategoryRequest request = new CategoryRequest("Updated Music", nonExistentUserId);
            
            when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.updateCategory(categoryId, request);
            }, "Should throw RuntimeException when user not found");
            
            assertEquals("User not found", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(nonExistentUserId);
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when category ID is null")
        void updateCategory_shouldThrowExceptionWhenCategoryIdIsNull() {
            // Arrange
            CategoryRequest request = new CategoryRequest("Updated Music", adminUserId);
            when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                categoryService.updateCategory(null, request);
            }, "Should throw NullPointerException when category ID is null");
            
            assertEquals("Category ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(adminUserId);
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when request is null")
        void updateCategory_shouldThrowExceptionWhenRequestIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                categoryService.updateCategory(categoryId, null);
            }, "Should throw NullPointerException when request is null");
            
            assertEquals("Category request cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when category name is null")
        void updateCategory_shouldThrowExceptionWhenNameIsNull() {
            // Arrange
            CategoryRequest request = new CategoryRequest(null, adminUserId);
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                categoryService.updateCategory(categoryId, request);
            }, "Should throw NullPointerException when name is null");
            
            assertEquals("Category name cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when userId is null")
        void updateCategory_shouldThrowExceptionWhenUserIdIsNull() {
            // Arrange
            CategoryRequest request = new CategoryRequest("Updated Music", null);
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                categoryService.updateCategory(categoryId, request);
            }, "Should throw NullPointerException when userId is null");
            
            assertEquals("User ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
    }
    
    @Nested
    @DisplayName("Delete Category Tests")
    class DeleteCategoryTests {
        
        @Test
        @DisplayName("Should successfully soft delete category when user is ADMIN")
        void deleteCategory_shouldSuccessWithAdminUser() {
            // Arrange
            EventCategory existingCategory = EventCategory.builder()
                    .id(categoryId)
                    .name("Music")
                    .active(true)
                    .build();
            
            when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
            when(eventCategoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
            when(eventCategoryRepository.save(any(EventCategory.class))).thenReturn(existingCategory);
            
            // Act
            categoryService.deleteCategory(categoryId, adminUserId);
            
            // Assert
            assertFalse(existingCategory.getActive(), "Category should be soft deleted (inactive)");
            
            verify(userRepository, times(1)).findById(adminUserId);
            verify(eventCategoryRepository, times(1)).findById(categoryId);
            verify(eventCategoryRepository, times(1)).save(existingCategory);
        }
        
        @Test
        @DisplayName("Should throw exception when category not found")
        void deleteCategory_shouldThrowExceptionWhenCategoryNotFound() {
            // Arrange
            UUID nonExistentCategoryId = UUID.randomUUID();
            
            when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
            when(eventCategoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.deleteCategory(nonExistentCategoryId, adminUserId);
            }, "Should throw RuntimeException when category not found");
            
            assertEquals("Category not found", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(adminUserId);
            verify(eventCategoryRepository, times(1)).findById(nonExistentCategoryId);
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when user is ATTENDEE - Access Denied")
        void deleteCategory_shouldThrowExceptionWhenUserIsAttendee() {
            // Arrange
            when(userRepository.findById(attendeeUserId)).thenReturn(Optional.of(attendeeUser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.deleteCategory(categoryId, attendeeUserId);
            }, "Should throw RuntimeException when user is ATTENDEE");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(attendeeUserId);
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when user is ORGANISER - Access Denied")
        void deleteCategory_shouldThrowExceptionWhenUserIsOrganiser() {
            // Arrange
            when(userRepository.findById(organiserUserId)).thenReturn(Optional.of(organiserUser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.deleteCategory(categoryId, organiserUserId);
            }, "Should throw RuntimeException when user is ORGANISER");
            
            assertEquals("Access denied", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(organiserUserId);
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw exception when user not found")
        void deleteCategory_shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            UUID nonExistentUserId = UUID.randomUUID();
            
            when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                categoryService.deleteCategory(categoryId, nonExistentUserId);
            }, "Should throw RuntimeException when user not found");
            
            assertEquals("User not found", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(nonExistentUserId);
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when category ID is null")
        void deleteCategory_shouldThrowExceptionWhenCategoryIdIsNull() {
            // Arrange
            when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                categoryService.deleteCategory(null, adminUserId);
            }, "Should throw NullPointerException when category ID is null");
            
            assertEquals("Category ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findById(adminUserId);
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when userId is null")
        void deleteCategory_shouldThrowExceptionWhenUserIdIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                categoryService.deleteCategory(categoryId, null);
            }, "Should throw NullPointerException when userId is null");
            
            assertEquals("User ID cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).findById(any());
            verify(eventCategoryRepository, never()).save(any(EventCategory.class));
        }
    }
}
