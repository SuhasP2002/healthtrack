package com.healthtrack.controller;

import com.healthtrack.dto.ReminderDTO;
import com.healthtrack.entity.User;
import com.healthtrack.repository.UserRepository;
import com.healthtrack.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    @Autowired private ReminderService reminderService;
    @Autowired private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ReminderDTO.ReminderResponse>> getReminders(Authentication auth) {
        return ResponseEntity.ok(reminderService.getUserReminders(getUserId(auth)));
    }

    @PostMapping
    public ResponseEntity<ReminderDTO.ReminderResponse> createReminder(
            Authentication auth, @RequestBody ReminderDTO.CreateReminderRequest req) {
        return ResponseEntity.ok(reminderService.createReminder(getUserId(auth), req));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<ReminderDTO.ReminderResponse> toggleReminder(
            Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(reminderService.toggleReminder(id, getUserId(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReminder(Authentication auth, @PathVariable Long id) {
        reminderService.deleteReminder(id, getUserId(auth));
        return ResponseEntity.ok("Reminder deleted");
    }

    private Long getUserId(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
