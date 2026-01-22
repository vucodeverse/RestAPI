package com.phongvu.restapi.repository;

import com.phongvu.restapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

    boolean existsByUsername(String username);
}
