package com.phongvu.restapi.controller;

import com.phongvu.restapi.constraint.SuccessCode;
import com.phongvu.restapi.dto.response.ApiResponse;
import com.phongvu.restapi.dto.response.UserSessionResponse;
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

    /**
     * Returns all active (non-revoked) sessions for the currently authenticated user.
     *
     * @return list of active sessions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserSessionResponse>>> getActiveSessions() {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.GET_SESSIONS_SUCCESS.getCode(),
                SuccessCode.GET_SESSIONS_SUCCESS.getMsg(),
                sessionService.getActiveSessions()
        ));
    }

    /**
     * Revokes (logs out) a specific session by its ID.
     *
     * @param sessionId the session ID to revoke
     * @return HTTP 200 on success
     */
    @PostMapping("{sessionId}/revoke")
    public ResponseEntity<ApiResponse<Void>> revokeSession(@PathVariable String sessionId) {
        sessionService.revokeSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.SESSION_REVOKED.getCode(),
                SuccessCode.SESSION_REVOKED.getMsg(),
                null));
    }
}
