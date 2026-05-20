package com.healthtrack.service;

import com.healthtrack.entity.Habit;
import com.healthtrack.entity.HabitLog;
import com.healthtrack.entity.User;
import com.healthtrack.repository.HabitLogRepository;
import com.healthtrack.repository.HabitRepository;
import com.healthtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WeeklyReportService {

    @Autowired private UserRepository userRepository;
    @Autowired private HabitRepository habitRepository;
    @Autowired private HabitLogRepository habitLogRepository;
    @Autowired private EmailService emailService;

    // Runs every Monday at 8:00 AM
    @Scheduled(cron = "0 0 8 * * MON")
    public void sendWeeklyReports() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            try {
                sendReportToUser(user);
            } catch (Exception e) {
                System.err.println("Failed to send weekly report to " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    public void sendReportToUser(User user) {
        LocalDate endDate = LocalDate.now().minusDays(1); // up to yesterday (Sunday)
        LocalDate startDate = endDate.minusDays(6);       // last 7 days

        List<Habit> habits = habitRepository.findByUserId(user.getId());
        List<HabitLog> logs = habitLogRepository.findByUserIdAndDateRange(user.getId(), startDate, endDate);

        long totalPossible = (long) habits.size() * 7;
        long totalCompleted = logs.stream().filter(l -> Boolean.TRUE.equals(l.getCompleted())).count();
        int completionRate = totalPossible > 0 ? (int) (totalCompleted * 100 / totalPossible) : 0;

        String subject = "Your HealthTrack Weekly Report 📊";
        String body = buildEmailBody(user.getName(), habits, logs, startDate, endDate,
                totalCompleted, totalPossible, completionRate);

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    private String buildEmailBody(String name, List<Habit> habits, List<HabitLog> logs,
                                   LocalDate start, LocalDate end,
                                   long completed, long total, int rate) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d");
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:sans-serif;max-width:600px;margin:auto;padding:20px;'>");
        sb.append("<h2 style='color:#22c55e;'>Weekly Habit Report 💚</h2>");
        sb.append("<p>Hi <strong>").append(name).append("</strong>,</p>");
        sb.append("<p>Here's your habit summary for <strong>")
          .append(start.format(fmt)).append(" – ").append(end.format(fmt)).append("</strong>:</p>");

        sb.append("<div style='background:#f0fdf4;border-radius:12px;padding:16px;margin:16px 0;'>");
        sb.append("<h3 style='margin:0 0 8px;'>Overall Completion: <span style='color:#22c55e;'>")
          .append(rate).append("%</span></h3>");
        sb.append("<p style='color:#64748b;margin:0;'>").append(completed).append(" of ").append(total)
          .append(" habit check-ins completed</p>");
        sb.append("</div>");

        if (!habits.isEmpty()) {
            sb.append("<h3>Habit Breakdown</h3><table width='100%' cellpadding='8' style='border-collapse:collapse;'>");
            sb.append("<tr style='background:#e5e7eb;'><th align='left'>Habit</th><th align='left'>Category</th><th align='right'>Days Done</th></tr>");
            for (Habit habit : habits) {
                long habitCompleted = logs.stream()
                        .filter(l -> l.getHabit().getId().equals(habit.getId()) && Boolean.TRUE.equals(l.getCompleted()))
                        .count();
                String color = habitCompleted >= 5 ? "#22c55e" : habitCompleted >= 3 ? "#f59e0b" : "#ef4444";
                sb.append("<tr style='border-bottom:1px solid #e5e7eb;'>");
                sb.append("<td>").append(habit.getName()).append("</td>");
                sb.append("<td style='color:#64748b;'>").append(habit.getCategory()).append("</td>");
                sb.append("<td align='right' style='color:").append(color).append(";font-weight:bold;'>")
                  .append(habitCompleted).append("/7</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }

        sb.append("<p style='color:#64748b;margin-top:24px;font-size:0.9rem;'>Keep it up! Consistency is key. 🔥</p>");
        sb.append("<p style='color:#94a3b8;font-size:0.8rem;'>— The HealthTrack Team</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
