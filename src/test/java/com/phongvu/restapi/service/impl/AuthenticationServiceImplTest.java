package com.phongvu.restapi.service.impl;

import com.phongvu.restapi.constraint.ErrorCode;
import com.phongvu.restapi.dto.request.AuthenticationRequest;
import com.phongvu.restapi.dto.request.ForgotPasswordRequest;
import com.phongvu.restapi.dto.request.ResetPasswordRequest;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.model.PasswordResetToken;
import com.phongvu.restapi.model.Role;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.model.UserSession;
import com.phongvu.restapi.repository.PasswordResetTokenRepository;
import com.phongvu.restapi.repository.UserRepository;
import com.phongvu.restapi.repository.UserSessionRepository;
import com.phongvu.restapi.service.MailService;
import com.phongvu.restapi.utils.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserSessionRepository userSessionRepository;
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private MailService mailService;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "secretKey", "test-very-long-secret-key-for-jwt-signing-test-environment-only-12345");
        ReflectionTestUtils.setField(authenticationService, "validDuration", 3600L);
        ReflectionTestUtils.setField(authenticationService, "refreshableDuration", 7200L);

        Role userRole = new Role();
        userRole.setName("USER");

        mockUser = User.builder()
                .id("u1")
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .is2faEnabled(false)
                .roles(Set.of(userRole))
                .build();
    }

    @Test
    void authenticate_Success() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("testuser", "rawPassword");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Test-Agent");

        // Act
        AuthenticationResponse response = authenticationService.authenticate(request, httpServletRequest);

        // Assert
        assertTrue(response.isAuthenticated());
        assertFalse(response.isMfaRequired());
        assertNotNull(response.getToken());

        verify(userSessionRepository, times(1)).save(any(UserSession.class));
    }

    @Test
    void authenticate_WrongPassword() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("testuser", "wrongPassword");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> 
            authenticationService.authenticate(request, httpServletRequest)
        );
        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
        verify(userSessionRepository, never()).save(any(UserSession.class));
    }

    @Test
    void forgotPassword_Success() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        // Act
        authenticationService.forgotPassword(request);

        // Assert
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository, times(1)).save(tokenCaptor.capture());
        PasswordResetToken savedToken = tokenCaptor.getValue();
        
        assertNotNull(savedToken.getTokenHash());
        assertEquals(mockUser, savedToken.getUser());
        assertFalse(savedToken.isUsed());
        assertTrue(savedToken.getExpiryDate().isAfter(LocalDateTime.now()));

        verify(mailService, times(1)).sendResetPasswordEmail(eq("test@example.com"), anyString());
    }

    @Test
    void forgotPassword_UserNotFound() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest("notfound@example.com");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppException.class, () -> authenticationService.forgotPassword(request));
        verify(passwordResetTokenRepository, never()).save(any());
        verify(mailService, never()).sendResetPasswordEmail(anyString(), anyString());
    }
}
