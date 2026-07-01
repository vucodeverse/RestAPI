package com.phongvu.restapi.repository;

import com.phongvu.restapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    // I4 Fix: @EntityGraph prevents N+1 when fetching roles + permissions
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findUserByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Page<User> findAll(Pageable pageable);
}
