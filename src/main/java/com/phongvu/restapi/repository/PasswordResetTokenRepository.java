package com.phongvu.restapi.repository;

import com.phongvu.restapi.model.PasswordResetToken;
import com.phongvu.restapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    List<PasswordResetToken> findByUserAndIsUsedFalse(User user);
    java.util.Optional<PasswordResetToken> findByTokenHashAndIsUsedFalse(String tokenHash);
}
