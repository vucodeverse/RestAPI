package com.phongvu.restapi.repository;

import com.phongvu.restapi.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    List<UserSession> findByUserIdAndIsRevokedFalse(String userId);
    List<UserSession> findByUser_UsernameAndIsRevokedFalse(String username);
}
