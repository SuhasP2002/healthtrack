package com.healthtrack.repository;

import com.healthtrack.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    // Get all habits for a specific user
    List<Habit> findByUserId(Long userId);

    // Get habit by id, but only if it belongs to this user (security check)
    Optional<Habit> findByIdAndUserId(Long id, Long userId);

    // Filter by category
    List<Habit> findByUserIdAndCategory(Long userId, String category);
}
