package com.phongvu.restapi.repository;

import com.phongvu.restapi.model.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepo extends JpaRepository<InvalidatedToken, String> {
}