package com.healthtrack.repository;

import com.healthtrack.entity.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    // Find log for a specific habit on a specific date
    Optional<HabitLog> findByHabitIdAndLogDate(Long habitId, LocalDate logDate);

    // Get all logs for a habit
    List<HabitLog> findByHabitId(Long habitId);

    // Get logs for a habit between two dates (used for analytics)
    List<HabitLog> findByHabitIdAndLogDateBetween(Long habitId, LocalDate startDate, LocalDate endDate);

    // Count completed logs in a date range (for streak calculation)
    @Query("SELECT COUNT(hl) FROM HabitLog hl WHERE hl.habit.id = :habitId " +
           "AND hl.completed = true " +
           "AND hl.logDate BETWEEN :startDate AND :endDate")
    long countCompletedBetween(@Param("habitId") Long habitId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    // Get all logs for a user's habits in a date range (for dashboard chart)
    @Query("SELECT hl FROM HabitLog hl WHERE hl.habit.user.id = :userId " +
           "AND hl.logDate BETWEEN :startDate AND :endDate " +
           "ORDER BY hl.logDate ASC")
    List<HabitLog> findByUserIdAndDateRange(@Param("userId") Long userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}
