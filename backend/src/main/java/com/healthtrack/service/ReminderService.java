package com.healthtrack.service;

import com.healthtrack.dto.ReminderDTO;
import com.healthtrack.entity.Reminder;
import com.healthtrack.entity.User;
import com.healthtrack.repository.ReminderRepository;
import com.healthtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderService {

    @Autowired private ReminderRepository reminderRepository;
    @Autowired private UserRepository userRepository;

    public ReminderDTO.ReminderResponse createReminder(Long userId, ReminderDTO.CreateReminderRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Reminder reminder = new Reminder();
        reminder.setUser(user);
        reminder.setReminderTime(req.getReminderTime());
        reminder.setEnabled(true);
        if (req.getMessage() != null && !req.getMessage().isBlank()) {
            reminder.setMessage(req.getMessage());
        }
        return mapToResponse(reminderRepository.save(reminder));
    }

    public List<ReminderDTO.ReminderResponse> getUserReminders(Long userId) {
        return reminderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public ReminderDTO.ReminderResponse toggleReminder(Long reminderId, Long userId) {
        Reminder reminder = reminderRepository.findByIdAndUserId(reminderId, userId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));
        reminder.setEnabled(!reminder.isEnabled());
        return mapToResponse(reminderRepository.save(reminder));
    }

    public void deleteReminder(Long reminderId, Long userId) {
        Reminder reminder = reminderRepository.findByIdAndUserId(reminderId, userId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));
        reminderRepository.delete(reminder);
    }

    private ReminderDTO.ReminderResponse mapToResponse(Reminder r) {
        ReminderDTO.ReminderResponse res = new ReminderDTO.ReminderResponse();
        res.setId(r.getId());
        res.setReminderTime(r.getReminderTime());
        res.setEnabled(r.isEnabled());
        res.setMessage(r.getMessage());
        return res;
    }
}
