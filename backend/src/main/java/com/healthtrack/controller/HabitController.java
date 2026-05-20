package com.healthtrack.controller;

import com.healthtrack.dto.HabitDTO;
import com.healthtrack.entity.HabitLog;
import com.healthtrack.entity.User;
import com.healthtrack.repository.UserRepository;
import com.healthtrack.service.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    @Autowired
    private HabitService habitService;

    @Autowired
    private UserRepository userRepository;

    // GET /api/habits — get all habits for logged-in user
    @GetMapping
    public ResponseEntity<List<HabitDTO.HabitResponse>> getHabits(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(habitService.getUserHabits(userId));
    }

    // POST /api/habits — create a new habit
    @PostMapping
    public ResponseEntity<HabitDTO.HabitResponse> createHabit(
            Authentication auth,
            @RequestBody HabitDTO.CreateHabitRequest request) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(habitService.createHabit(userId, request));
    }

    // DELETE /api/habits/{id} — delete a habit
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHabit(Authentication auth, @PathVariable Long id) {
        Long userId = getUserId(auth);
        habitService.deleteHabit(id, userId);
        return ResponseEntity.ok("Habit deleted successfully");
    }

    // POST /api/habits/{id}/log — mark habit as completed
    @PostMapping("/{id}/log")
    public ResponseEntity<HabitLog> logHabit(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody HabitDTO.LogHabitRequest request) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(habitService.logHabit(id, userId, request));
    }

    // GET /api/habits/stats — get weekly analytics for dashboard chart
    @GetMapping("/stats")
    public ResponseEntity<List<HabitDTO.WeeklyStatsResponse>> getWeeklyStats(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(habitService.getWeeklyStats(userId));
    }

    // Helper: get user ID from the JWT authentication object
    private Long getUserId(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
