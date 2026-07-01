package com.phongvu.restapi.service.impl;

import com.phongvu.restapi.dto.response.UserSessionResponse;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.model.UserSession;
import com.phongvu.restapi.repository.UserSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock private UserSessionRepository userSessionRepository;
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private SessionServiceImpl sessionService;

    private UserSession mockSession;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id("u1")
                .username("testuser")
                .build();

        mockSession = UserSession.builder()
                .id("session-uuid-1")
                .user(mockUser)
                .deviceInfo("Mozilla/5.0 Test-Agent")
                .ipAddress("127.0.0.1")
                .isRevoked(false)
                .build();
    }

    @Test
    void getActiveSessions_WithJwtPrincipal_ReturnsSessionList() {
        // Arrange
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getSubject()).thenReturn("testuser");

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(mockJwt);

        SecurityContext mockContext = mock(SecurityContext.class);
        when(mockContext.getAuthentication()).thenReturn(mockAuth);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(mockContext);

            when(userSessionRepository.findByUser_UsernameAndIsRevokedFalse("testuser"))
                    .thenReturn(List.of(mockSession));

            // Act
            List<UserSessionResponse> result = sessionService.getActiveSessions();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("session-uuid-1", result.get(0).getId());
            assertEquals("Mozilla/5.0 Test-Agent", result.get(0).getDeviceInfo());
            assertEquals("127.0.0.1", result.get(0).getIpAddress());
        }
    }

    @Test
    void getActiveSessions_WhenNoActiveSessions_ReturnsEmptyList() {
        // Arrange
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getSubject()).thenReturn("testuser");

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(mockJwt);

        SecurityContext mockContext = mock(SecurityContext.class);
        when(mockContext.getAuthentication()).thenReturn(mockAuth);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(mockContext);
            when(userSessionRepository.findByUser_UsernameAndIsRevokedFalse("testuser"))
                    .thenReturn(List.of());

            // Act
            List<UserSessionResponse> result = sessionService.getActiveSessions();

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void revokeSession_WhenSessionExists_RevokesAndBlacklists() {
        // Arrange
        when(userSessionRepository.findById("session-uuid-1")).thenReturn(Optional.of(mockSession));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        sessionService.revokeSession("session-uuid-1");

        // Assert
        assertTrue(mockSession.isRevoked());
        verify(userSessionRepository, times(1)).save(mockSession);
        verify(valueOperations, times(1)).set(
                eq("blacklist:session:session-uuid-1"),
                eq("revoked"),
                anyLong(),
                any()
        );
    }

    @Test
    void revokeSession_WhenSessionNotFound_DoesNothing() {
        // Arrange
        when(userSessionRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        sessionService.revokeSession("nonexistent");

        // Assert
        verify(userSessionRepository, never()).save(any());
        verify(redisTemplate, never()).opsForValue();
    }
}
