package com.healthtrack.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class HabitDTO {

    @Data
    public static class CreateHabitRequest {
        private String name;
        private String description;
        private String category;
        private String frequency;
        private Integer targetCount;
    }

    @Data
    public static class HabitResponse {
        private Long id;
        private String name;
        private String description;
        private String category;
        private String frequency;
        private Integer targetCount;
        private LocalDateTime createdAt;
        private int completionRate;
        private int streak;
    }

    @Data
    public static class LogHabitRequest {
        private LocalDate logDate;
        private Boolean completed;
        private String notes;
    }

    @Data
    public static class WeeklyStatsResponse {
        private String date;
        private long totalHabits;
        private long completedHabits;
        private double completionRate;
    }

    @Data
    public static class UserStatsResponse {
        private int totalHabits;
        private int totalCompletions;
        private int bestStreak;
        private int weeklyCompletionRate;
    }
}
