package com.phongvu.restapi.service.impl;

import com.phongvu.restapi.model.UserSession;
import com.phongvu.restapi.repository.UserSessionRepository;
import com.phongvu.restapi.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final UserSessionRepository userSessionRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public List<UserSession> getActiveSessions() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof Jwt) {
            username = ((Jwt) principal).getSubject();
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        return userSessionRepository.findByUser_UsernameAndIsRevokedFalse(username);
    }

    @Override
    public void revokeSession(String sessionId) {
        userSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setRevoked(true);
            userSessionRepository.save(session);
            redisTemplate.opsForValue().set("blacklist:session:" + sessionId, "revoked", 1, TimeUnit.HOURS);
        });
    }
}
