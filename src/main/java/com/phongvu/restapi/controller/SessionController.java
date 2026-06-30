package com.phongvu.restapi.controller;

import com.phongvu.restapi.dto.response.ApiResponse;
import com.phongvu.restapi.dto.response.UserSessionResponse;
import com.phongvu.restapi.model.UserSession;
import com.phongvu.restapi.service.SessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "User Sessions")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserSessionResponse>>> getActiveSessions() {
        List<UserSessionResponse> sessions = sessionService.getActiveSessions();
        return ResponseEntity.ok(ApiResponse.success(
                200,
                "Lấy danh sách thiết bị thành công",
                sessions
        ));
    }

    @PostMapping("{sessionId}/revoke")
    public ResponseEntity<ApiResponse<Void>> revokeSession(@PathVariable String sessionId) {
        sessionService.revokeSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success(200, "Đăng xuất thiết bị thành công", null));
    }
}
