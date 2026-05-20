package com.healthtrack.service;

import com.healthtrack.dto.HabitDTO;
import com.healthtrack.entity.Habit;
import com.healthtrack.entity.HabitLog;
import com.healthtrack.entity.User;
import com.healthtrack.repository.HabitLogRepository;
import com.healthtrack.repository.HabitRepository;
import com.healthtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HabitService {

    @Autowired private HabitRepository habitRepository;
    @Autowired private HabitLogRepository habitLogRepository;
    @Autowired private UserRepository userRepository;

    public HabitDTO.HabitResponse createHabit(Long userId, HabitDTO.CreateHabitRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setCategory(request.getCategory());
        habit.setFrequency(request.getFrequency());
        habit.setTargetCount(request.getTargetCount() != null ? request.getTargetCount() : 1);
        habit.setUser(user);
        return mapToResponse(habitRepository.save(habit));
    }

    public List<HabitDTO.HabitResponse> getUserHabits(Long userId) {
        return habitRepository.findByUserId(userId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public void deleteHabit(Long habitId, Long userId) {
        Habit habit = habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        habitRepository.delete(habit);
    }

    public HabitLog logHabit(Long habitId, Long userId, HabitDTO.LogHabitRequest request) {
        Habit habit = habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        LocalDate date = request.getLogDate() != null ? request.getLogDate() : LocalDate.now();
        HabitLog log = habitLogRepository.findByHabitIdAndLogDate(habitId, date).orElse(new HabitLog());
        log.setHabit(habit);
        log.setLogDate(date);
        log.setCompleted(request.getCompleted() != null ? request.getCompleted() : true);
        log.setNotes(request.getNotes());
        return habitLogRepository.save(log);
    }

    public List<HabitDTO.WeeklyStatsResponse> getWeeklyStats(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        List<Habit> userHabits = habitRepository.findByUserId(userId);
        List<HabitLog> logs = habitLogRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        List<HabitDTO.WeeklyStatsResponse> stats = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate d = date;
            long total = userHabits.size();
            long completed = logs.stream().filter(l -> l.getLogDate().equals(d) && l.getCompleted()).count();
            HabitDTO.WeeklyStatsResponse stat = new HabitDTO.WeeklyStatsResponse();
            stat.setDate(date.toString());
            stat.setTotalHabits(total);
            stat.setCompletedHabits(completed);
            stat.setCompletionRate(total > 0 ? (completed * 100.0 / total) : 0);
            stats.add(stat);
        }
        return stats;
    }

    public int calculateStreak(Long habitId) {
        LocalDate today = LocalDate.now();
        int streak = 0;
        for (int i = 0; i < 365; i++) {
            LocalDate checkDate = today.minusDays(i);
            boolean completed = habitLogRepository.findByHabitIdAndLogDate(habitId, checkDate)
                    .map(log -> Boolean.TRUE.equals(log.getCompleted())).orElse(false);
            if (completed) streak++;
            else break;
        }
        return streak;
    }

    public HabitDTO.UserStatsResponse getUserStats(Long userId) {
        List<Habit> habits = habitRepository.findByUserId(userId);
        long totalCompletions = habits.stream()
                .mapToLong(h -> habitLogRepository.countCompletedBetween(
                        h.getId(), LocalDate.now().minusYears(1), LocalDate.now()))
                .sum();
        int bestStreak = habits.stream().mapToInt(h -> calculateStreak(h.getId())).max().orElse(0);
        List<HabitLog> weekLogs = habitLogRepository.findByUserIdAndDateRange(
                userId, LocalDate.now().minusDays(6), LocalDate.now());
        long weekCompleted = weekLogs.stream().filter(l -> Boolean.TRUE.equals(l.getCompleted())).count();
        long weekTotal = (long) habits.size() * 7;
        int weeklyRate = weekTotal > 0 ? (int) (weekCompleted * 100 / weekTotal) : 0;

        HabitDTO.UserStatsResponse stats = new HabitDTO.UserStatsResponse();
        stats.setTotalHabits(habits.size());
        stats.setTotalCompletions((int) totalCompletions);
        stats.setBestStreak(bestStreak);
        stats.setWeeklyCompletionRate(weeklyRate);
        return stats;
    }

    private HabitDTO.HabitResponse mapToResponse(Habit habit) {
        HabitDTO.HabitResponse r = new HabitDTO.HabitResponse();
        r.setId(habit.getId());
        r.setName(habit.getName());
        r.setDescription(habit.getDescription());
        r.setCategory(habit.getCategory());
        r.setFrequency(habit.getFrequency());
        r.setTargetCount(habit.getTargetCount());
        r.setCreatedAt(habit.getCreatedAt());
        long completed = habitLogRepository.countCompletedBetween(
                habit.getId(), LocalDate.now().minusDays(6), LocalDate.now());
        r.setCompletionRate((int) (completed * 100 / 7));
        r.setStreak(calculateStreak(habit.getId()));
        return r;
    }
}
