package com.phongvu.restapi.controller;

import com.phongvu.restapi.constraint.SuccessCode;
import com.phongvu.restapi.dto.response.ApiResponse;
import com.phongvu.restapi.model.UserSession;
import com.phongvu.restapi.repository.UserSessionRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/sessions")
@RequiredArgsConstructor
@Tag(name = "User Sessions")
public class SessionController {

    private final UserSessionRepository userSessionRepository;
    private final StringRedisTemplate redisTemplate;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserSession>>> getActiveSessions() {
        // Lấy username từ context
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof Jwt) {
            username = ((Jwt) principal).getSubject();
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        // Trong thực tế, bạn nên truy vấn user id từ username, tạm thời giả định user_id có thể map hoặc join qua user.
        List<UserSession> sessions = userSessionRepository.findByUser_UsernameAndIsRevokedFalse(username);
        return ResponseEntity.ok(ApiResponse.success(
                200,
                "Lấy danh sách thiết bị thành công",
                sessions
        ));
    }

    @PostMapping("{sessionId}/revoke")
    public ResponseEntity<ApiResponse<Void>> revokeSession(@PathVariable String sessionId) {
        userSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setRevoked(true);
            userSessionRepository.save(session);
            // Đưa vào blacklist, thời gian TTL có thể đặt cố định bằng expiration tối đa của Access Token (VD: 1 giờ)
            redisTemplate.opsForValue().set("blacklist:session:" + sessionId, "revoked", 1, TimeUnit.HOURS);
        });
        return ResponseEntity.ok(ApiResponse.success(200, "Đăng xuất thiết bị thành công", null));
    }
}
