package com.phongvu.restapi.service;

import com.phongvu.restapi.model.UserSession;
import java.util.List;

public interface SessionService {
    List<UserSession> getActiveSessions();
    void revokeSession(String sessionId);
}
