package com.eventzone.eventzone_backend.service;

import com.eventzone.eventzone_backend.dto.LoginRequest;
import com.eventzone.eventzone_backend.dto.LoginResponse;
import com.eventzone.eventzone_backend.dto.RegisterRequest;
import com.eventzone.eventzone_backend.dto.RegisterResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
@SuppressWarnings("null")
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AuthService authService;
    
    private UUID testUserId;
    
    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
    }
    
    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {
        
        @Test
        @DisplayName("Should successfully register new user with valid data")
        void register_shouldCreateUserSuccessfully() {
            // Arrange
            RegisterRequest request = RegisterRequest.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();
            
            User savedUser = User.builder()
                    .id(testUserId)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("encodedPassword")
                    .role("ATTENDEE")
                    .build();
            
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            
            // Act
            RegisterResponse response = authService.register(request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(testUserId, response.getId(), "User ID should match");
            assertEquals("john.doe@example.com", response.getEmail(), "Email should match");
            assertEquals("John Doe", response.getName(), "Name should match");
            assertEquals("ATTENDEE", response.getRole(), "Role should be ATTENDEE by default");
            
            verify(userRepository, times(1)).findByEmail("john.doe@example.com");
            verify(passwordEncoder, times(1)).encode("password123");
            verify(userRepository, times(1)).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should throw exception when email already exists")
        void register_shouldThrowExceptionWhenEmailAlreadyExists() {
            // Arrange
            RegisterRequest request = RegisterRequest.builder()
                    .name("John Doe")
                    .email("existing@example.com")
                    .password("password123")
                    .build();
            
            User existingUser = User.builder()
                    .id(testUserId)
                    .name("Existing User")
                    .email("existing@example.com")
                    .password("encodedPassword")
                    .role("ATTENDEE")
                    .build();
            
            when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                authService.register(request);
            }, "Should throw RuntimeException when email already exists");
            
            assertEquals("Email already registered", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findByEmail("existing@example.com");
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when register request is null")
        void register_shouldThrowExceptionWhenRequestIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                authService.register(null);
            }, "Should throw NullPointerException when request is null");
            
            assertEquals("Register request cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findByEmail(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when email is null")
        void register_shouldThrowExceptionWhenEmailIsNull() {
            // Arrange
            RegisterRequest request = RegisterRequest.builder()
                    .name("John Doe")
                    .email(null)
                    .password("password123")
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                authService.register(request);
            }, "Should throw NullPointerException when email is null");
            
            assertEquals("Email cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findByEmail(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when name is null")
        void register_shouldThrowExceptionWhenNameIsNull() {
            // Arrange
            RegisterRequest request = RegisterRequest.builder()
                    .name(null)
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                authService.register(request);
            }, "Should throw NullPointerException when name is null");
            
            assertEquals("Name cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findByEmail(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when password is null")
        void register_shouldThrowExceptionWhenPasswordIsNull() {
            // Arrange
            RegisterRequest request = RegisterRequest.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password(null)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                authService.register(request);
            }, "Should throw NullPointerException when password is null");
            
            assertEquals("Password cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findByEmail(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should encode password before saving user")
        void register_shouldEncodePasswordBeforeSaving() {
            // Arrange
            RegisterRequest request = RegisterRequest.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("plainPassword")
                    .build();
            
            User savedUser = User.builder()
                    .id(testUserId)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("encodedPassword")
                    .role("ATTENDEE")
                    .build();
            
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            
            // Act
            authService.register(request);
            
            // Assert
            verify(passwordEncoder, times(1)).encode("plainPassword");
        }
    }
    
    @Nested
    @DisplayName("Login Tests")
    class LoginTests {
        
        @Test
        @DisplayName("Should successfully login with valid credentials")
        void login_shouldReturnSuccessWhenCredentialsValid() {
            // Arrange
            LoginRequest request = LoginRequest.builder()
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();
            
            User user = User.builder()
                    .id(testUserId)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("encodedPassword")
                    .role("ATTENDEE")
                    .build();
            
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            
            // Act
            LoginResponse response = authService.login(request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals("Login successful", response.getMessage(), "Message should match");
            assertEquals(testUserId, response.getUserId(), "User ID should match");
            assertEquals("john.doe@example.com", response.getEmail(), "Email should match");
            assertEquals("ATTENDEE", response.getRole(), "Role should match");
            
            verify(userRepository, times(1)).findByEmail("john.doe@example.com");
            verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        }
        
        @Test
        @DisplayName("Should throw exception when user not found by email")
        void login_shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            LoginRequest request = LoginRequest.builder()
                    .email("nonexistent@example.com")
                    .password("password123")
                    .build();
            
            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                authService.login(request);
            }, "Should throw RuntimeException when user not found");
            
            assertEquals("Invalid email or password", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
        
        @Test
        @DisplayName("Should throw exception when password is invalid")
        void login_shouldThrowExceptionWhenPasswordInvalid() {
            // Arrange
            LoginRequest request = LoginRequest.builder()
                    .email("john.doe@example.com")
                    .password("wrongpassword")
                    .build();
            
            User user = User.builder()
                    .id(testUserId)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("encodedPassword")
                    .role("ATTENDEE")
                    .build();
            
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                authService.login(request);
            }, "Should throw RuntimeException when password is invalid");
            
            assertEquals("Invalid email or password", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findByEmail("john.doe@example.com");
            verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when login request is null")
        void login_shouldThrowExceptionWhenRequestIsNull() {
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                authService.login(null);
            }, "Should throw NullPointerException when request is null");
            
            assertEquals("Login request cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findByEmail(anyString());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when email is null in login request")
        void login_shouldThrowExceptionWhenEmailIsNull() {
            // Arrange
            LoginRequest request = LoginRequest.builder()
                    .email(null)
                    .password("password123")
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                authService.login(request);
            }, "Should throw NullPointerException when email is null");
            
            assertEquals("Email cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findByEmail(anyString());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when password is null in login request")
        void login_shouldThrowExceptionWhenPasswordIsNull() {
            // Arrange
            LoginRequest request = LoginRequest.builder()
                    .email("john.doe@example.com")
                    .password(null)
                    .build();
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                authService.login(request);
            }, "Should throw NullPointerException when password is null");
            
            assertEquals("Password cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, never()).findByEmail(anyString());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
        
        @Test
        @DisplayName("Should throw NullPointerException when user password in database is null")
        void login_shouldThrowExceptionWhenUserPasswordIsNull() {
            // Arrange
            LoginRequest request = LoginRequest.builder()
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();
            
            User user = User.builder()
                    .id(testUserId)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password(null)
                    .role("ATTENDEE")
                    .build();
            
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
            
            // Act & Assert
            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                authService.login(request);
            }, "Should throw NullPointerException when user password is null");
            
            assertEquals("User password cannot be null", exception.getMessage(), "Exception message should match");
            
            verify(userRepository, times(1)).findByEmail("john.doe@example.com");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
        
        @Test
        @DisplayName("Should successfully login user with ORGANISER role")
        void login_shouldSuccessfullyLoginOrganiser() {
            // Arrange
            LoginRequest request = LoginRequest.builder()
                    .email("organiser@example.com")
                    .password("password123")
                    .build();
            
            User user = User.builder()
                    .id(testUserId)
                    .name("Organiser User")
                    .email("organiser@example.com")
                    .password("encodedPassword")
                    .role("ORGANISER")
                    .build();
            
            when(userRepository.findByEmail("organiser@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            
            // Act
            LoginResponse response = authService.login(request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals("Login successful", response.getMessage(), "Message should match");
            assertEquals(testUserId, response.getUserId(), "User ID should match");
            assertEquals("organiser@example.com", response.getEmail(), "Email should match");
            assertEquals("ORGANISER", response.getRole(), "Role should be ORGANISER");
            
            verify(userRepository, times(1)).findByEmail("organiser@example.com");
            verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        }
        
        @Test
        @DisplayName("Should successfully login user with ADMIN role")
        void login_shouldSuccessfullyLoginAdmin() {
            // Arrange
            LoginRequest request = LoginRequest.builder()
                    .email("admin@example.com")
                    .password("password123")
                    .build();
            
            User user = User.builder()
                    .id(testUserId)
                    .name("Admin User")
                    .email("admin@example.com")
                    .password("encodedPassword")
                    .role("ADMIN")
                    .build();
            
            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            
            // Act
            LoginResponse response = authService.login(request);
            
            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals("Login successful", response.getMessage(), "Message should match");
            assertEquals(testUserId, response.getUserId(), "User ID should match");
            assertEquals("admin@example.com", response.getEmail(), "Email should match");
            assertEquals("ADMIN", response.getRole(), "Role should be ADMIN");
            
            verify(userRepository, times(1)).findByEmail("admin@example.com");
            verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        }
    }
}
