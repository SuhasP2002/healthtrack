package com.healthtrack.dto;

import lombok.Data;
import java.time.LocalTime;

public class ReminderDTO {

    @Data
    public static class CreateReminderRequest {
        private LocalTime reminderTime;
        private String message;
    }

    @Data
    public static class ReminderResponse {
        private Long id;
        private LocalTime reminderTime;
        private boolean enabled;
        private String message;
    }
}
