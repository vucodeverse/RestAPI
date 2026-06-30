package com.phongvu.restapi.service;

import com.phongvu.restapi.dto.response.UserSessionResponse;
import com.phongvu.restapi.model.UserSession;
import java.util.List;

public interface SessionService {
    List<UserSessionResponse> getActiveSessions();
    void revokeSession(String sessionId);
}
