package com.eventzone.eventzone_backend.service;

import com.eventzone.eventzone_backend.dto.UpdateRoleRequest;
import com.eventzone.eventzone_backend.dto.UserResponse;
import com.eventzone.eventzone_backend.entity.User;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
@SuppressWarnings("null")
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    private UUID testUserId;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(testUserId)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role("ATTENDEE")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Nested
    @DisplayName("Update User Role Tests")
    class UpdateUserRoleTests {
        
        @Test
        @DisplayName("Should successfully update user role from ATTENDEE to ORGANISER")
        void updateUserRole_shouldUpdateRoleSuccessfully_whenValidDataProvided() {
            // Arrange
            UpdateRoleRequest request = UpdateRoleRequest.builder()
                    .role("ORGANISER")
                    .build();
            
            User updatedUser = User.builder()
                    .id(testUserId)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("encodedPassword")
                    .role("ORGANISER")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            
            // Act
            UserResponse response = userService.updateUserRole(testUserId, request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(testUserId, response.getId(), "User ID should match");
            assertEquals("John Doe", response.getName(), "Name should match");
            assertEquals("john.doe@example.com", response.getEmail(), "Email should match");
            assertEquals("ORGANISER", response.getRole(), "Role should be updated to ORGANISER");
            
            verify(userRepository, times(1)).findById(testUserId);
            verify(userRepository, times(1)).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should successfully update user role from ATTENDEE to ADMIN")
        void updateUserRole_shouldUpdateToAdmin_whenValidDataProvided() {
            // Arrange
            UpdateRoleRequest request = UpdateRoleRequest.builder()
                    .role("ADMIN")
                    .build();
            
            User updatedUser = User.builder()
                    .id(testUserId)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("encodedPassword")
                    .role("ADMIN")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            
            // Act
            UserResponse response = userService.updateUserRole(testUserId, request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(testUserId, response.getId(), "User ID should match");
            assertEquals("ADMIN", response.getRole(), "Role should be updated to ADMIN");
            
            verify(userRepository, times(1)).findById(testUserId);
            verify(userRepository, times(1)).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should successfully update user role from ORGANISER to ATTENDEE")
        void updateUserRole_shouldDowngradeRole_whenValidDataProvided() {
            // Arrange
            testUser.setRole("ORGANISER");
            
            UpdateRoleRequest request = UpdateRoleRequest.builder()
                    .role("ATTENDEE")
                    .build();
            
            User updatedUser = User.builder()
                    .id(testUserId)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("encodedPassword")
                    .role("ATTENDEE")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            
            // Act
            UserResponse response = userService.updateUserRole(testUserId, request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals("ATTENDEE", response.getRole(), "Role should be downgraded to ATTENDEE");
            
            verify(userRepository, times(1)).findById(testUserId);
            verify(userRepository, times(1)).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when userId is null")
        void updateUserRole_shouldThrowException_whenUserIdIsNull() {
            // Arrange
            UpdateRoleRequest request = UpdateRoleRequest.builder()
                    .role("ORGANISER")
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> userService.updateUserRole(null, request),
                    "Should throw NullPointerException when userId is null"
            );
            
            assertEquals("User ID cannot be null", exception.getMessage());
            
            verify(userRepository, never()).findById(any());
            verify(userRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when request is null")
        void updateUserRole_shouldThrowException_whenRequestIsNull() {
            // Arrange
            // No request object
            
            // Act & Assert
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> userService.updateUserRole(testUserId, null),
                    "Should throw NullPointerException when request is null"
            );
            
            assertEquals("Update role request cannot be null", exception.getMessage());
            
            verify(userRepository, never()).findById(any());
            verify(userRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when role in request is null")
        void updateUserRole_shouldThrowException_whenRoleIsNull() {
            // Arrange
            UpdateRoleRequest request = UpdateRoleRequest.builder()
                    .role(null)
                    .build();
            
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            
            // Act & Assert
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> userService.updateUserRole(testUserId, request),
                    "Should throw NullPointerException when role is null"
            );
            
            assertEquals("Role cannot be null", exception.getMessage());
            
            verify(userRepository, times(1)).findById(testUserId);
            verify(userRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Should throw RuntimeException when user not found")
        void updateUserRole_shouldThrowException_whenUserNotFound() {
            // Arrange
            UUID nonExistentUserId = UUID.randomUUID();
            UpdateRoleRequest request = UpdateRoleRequest.builder()
                    .role("ORGANISER")
                    .build();
            
            when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> userService.updateUserRole(nonExistentUserId, request),
                    "Should throw RuntimeException when user not found"
            );
            
            assertEquals("User not found with id: " + nonExistentUserId, exception.getMessage());
            
            verify(userRepository, times(1)).findById(nonExistentUserId);
            verify(userRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Should verify repository interactions in correct order")
        void updateUserRole_shouldInvokeRepositoryMethodsInCorrectOrder() {
            // Arrange
            UpdateRoleRequest request = UpdateRoleRequest.builder()
                    .role("ORGANISER")
                    .build();
            
            User updatedUser = User.builder()
                    .id(testUserId)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("encodedPassword")
                    .role("ORGANISER")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            
            // Act
            userService.updateUserRole(testUserId, request);
            
            // Assert - Verify order of operations
            var inOrder = inOrder(userRepository);
            inOrder.verify(userRepository).findById(testUserId);
            inOrder.verify(userRepository).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should update role and preserve other user properties")
        void updateUserRole_shouldPreserveOtherProperties_whenUpdatingRole() {
            // Arrange
            UpdateRoleRequest request = UpdateRoleRequest.builder()
                    .role("ORGANISER")
                    .build();
            
            User updatedUser = User.builder()
                    .id(testUserId)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("encodedPassword")
                    .role("ORGANISER")
                    .active(true)
                    .createdAt(testUser.getCreatedAt())
                    .build();
            
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            
            // Act
            UserResponse response = userService.updateUserRole(testUserId, request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(testUserId, response.getId(), "ID should be preserved");
            assertEquals("John Doe", response.getName(), "Name should be preserved");
            assertEquals("john.doe@example.com", response.getEmail(), "Email should be preserved");
            assertEquals("ORGANISER", response.getRole(), "Role should be updated");
            
            verify(userRepository, times(1)).findById(testUserId);
            verify(userRepository, times(1)).save(argThat(user ->
                    user.getId().equals(testUserId) &&
                    user.getName().equals("John Doe") &&
                    user.getEmail().equals("john.doe@example.com") &&
                    user.getRole().equals("ORGANISER")
            ));
        }
        
        @Test
        @DisplayName("Should handle repository save returning different object reference")
        void updateUserRole_shouldHandleDifferentObjectReference_fromRepository() {
            // Arrange
            UpdateRoleRequest request = UpdateRoleRequest.builder()
                    .role("ADMIN")
                    .build();
            
            // Create a different object instance for the saved user
            User differentUserInstance = User.builder()
                    .id(testUserId)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("encodedPassword")
                    .role("ADMIN")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(differentUserInstance);
            
            // Act
            UserResponse response = userService.updateUserRole(testUserId, request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(testUserId, response.getId(), "Should use data from saved instance");
            assertEquals("ADMIN", response.getRole(), "Should reflect updated role from saved instance");
            
            verify(userRepository, times(1)).findById(testUserId);
            verify(userRepository, times(1)).save(testUser);
        }
        
        @Test
        @DisplayName("Should work with different valid UUID formats")
        void updateUserRole_shouldWorkWithDifferentUUIDs() {
            // Arrange
            UUID randomUUID = UUID.randomUUID();
            User anotherUser = User.builder()
                    .id(randomUUID)
                    .name("Jane Smith")
                    .email("jane.smith@example.com")
                    .password("encodedPassword")
                    .role("ATTENDEE")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            UpdateRoleRequest request = UpdateRoleRequest.builder()
                    .role("ORGANISER")
                    .build();
            
            User updatedUser = User.builder()
                    .id(randomUUID)
                    .name("Jane Smith")
                    .email("jane.smith@example.com")
                    .password("encodedPassword")
                    .role("ORGANISER")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            when(userRepository.findById(randomUUID)).thenReturn(Optional.of(anotherUser));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            
            // Act
            UserResponse response = userService.updateUserRole(randomUUID, request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(randomUUID, response.getId(), "Should handle different UUID");
            assertEquals("Jane Smith", response.getName(), "Name should match");
            assertEquals("ORGANISER", response.getRole(), "Role should be updated");
            
            verify(userRepository, times(1)).findById(randomUUID);
            verify(userRepository, times(1)).save(any(User.class));
        }
    }
}
