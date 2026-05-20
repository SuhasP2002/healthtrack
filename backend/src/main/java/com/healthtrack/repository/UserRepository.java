package com.healthtrack.repository;

import com.healthtrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// JpaRepository gives us free CRUD methods: save(), findById(), findAll(), delete(), etc.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA auto-generates SQL: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Check if email is already registered
    boolean existsByEmail(String email);
}
