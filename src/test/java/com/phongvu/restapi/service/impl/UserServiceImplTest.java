package com.phongvu.restapi.service.impl;

import com.phongvu.restapi.constraint.ErrorCode;
import com.phongvu.restapi.dto.request.UserCreationRequest;
import com.phongvu.restapi.dto.response.UserResponse;
import com.phongvu.restapi.mapper.UserMapper;
import com.phongvu.restapi.model.Role;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.repository.RoleRepository;
import com.phongvu.restapi.repository.UserRepository;
import com.phongvu.restapi.utils.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private UserResponse mockUserResponse;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setName("USER");

        mockUser = User.builder()
                .id("u1")
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .is2faEnabled(false)
                .roles(Set.of(userRole))
                .build();

        mockUserResponse = new UserResponse();
        mockUserResponse.setId("u1");
        mockUserResponse.setUsername("testuser");
    }

    // ─── createUser ──────────────────────────────────────────────────────────

    @Test
    void createUser_Success() {
        // Arrange
        UserCreationRequest request = new UserCreationRequest("testuser", "password123", "Test User", null);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(mockUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toUserResponse(mockUser)).thenReturn(mockUserResponse);

        // Act
        UserResponse result = userService.createUser(request);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void createUser_UsernameAlreadyExists_ThrowsException() {
        // Arrange
        UserCreationRequest request = new UserCreationRequest("testuser", "password123", null, null);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        AppException ex = assertThrows(AppException.class, () -> userService.createUser(request));
        assertEquals(ErrorCode.USER_EXISTED, ex.getErrorCode());
        verify(userRepository, never()).save(any());
    }

    // ─── getUserById ─────────────────────────────────────────────────────────

    @Test
    void getUserById_WhenUserExists_ReturnsUser() {
        // Arrange
        when(userRepository.findById("u1")).thenReturn(Optional.of(mockUser));
        when(userMapper.toUserResponse(mockUser)).thenReturn(mockUserResponse);

        // Act
        UserResponse result = userService.getUserById("u1");

        // Assert
        assertNotNull(result);
        assertEquals("u1", result.getId());
    }

    @Test
    void getUserById_WhenUserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        AppException ex = assertThrows(AppException.class, () -> userService.getUserById("nonexistent"));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    // ─── deleteUser ──────────────────────────────────────────────────────────

    @Test
    void deleteUser_WhenUserExists_DeletesSuccessfully() {
        // Arrange
        when(userRepository.existsById("u1")).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> userService.deleteUser("u1"));

        // Assert
        verify(userRepository, times(1)).deleteById("u1");
    }

    @Test
    void deleteUser_WhenUserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.existsById("nonexistent")).thenReturn(false);

        // Act & Assert
        AppException ex = assertThrows(AppException.class, () -> userService.deleteUser("nonexistent"));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        verify(userRepository, never()).deleteById(any());
    }
}
