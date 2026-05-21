package com.healthtrack.service;

import com.healthtrack.dto.HabitDTO;
import com.healthtrack.entity.Habit;
import com.healthtrack.entity.HabitLog;
import com.healthtrack.entity.User;
import com.healthtrack.repository.HabitLogRepository;
import com.healthtrack.repository.HabitRepository;
import com.healthtrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock private HabitRepository habitRepository;
    @Mock private HabitLogRepository habitLogRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private HabitService habitService;

    private User testUser;
    private Habit testHabit;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testHabit = new Habit();
        testHabit.setId(1L);
        testHabit.setName("Exercise");
        testHabit.setDescription("Daily workout");
        testHabit.setCategory("fitness");
        testHabit.setFrequency("daily");
        testHabit.setTargetCount(1);
        testHabit.setUser(testUser);
        testHabit.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createHabit_ShouldReturnHabitResponse_WhenValidRequest() {
        HabitDTO.CreateHabitRequest request = new HabitDTO.CreateHabitRequest();
        request.setName("Exercise");
        request.setDescription("Daily workout");
        request.setCategory("fitness");
        request.setFrequency("daily");
        request.setTargetCount(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);
        when(habitLogRepository.countCompletedBetween(anyLong(), any(), any())).thenReturn(0L);
        when(habitLogRepository.findByHabitIdAndLogDate(anyLong(), any())).thenReturn(Optional.empty());
        HabitDTO.HabitResponse response = habitService.createHabit(1L, request);
        assertNotNull(response);
        assertEquals("Exercise", response.getName());
        assertEquals("fitness", response.getCategory());
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    void createHabit_ShouldThrowException_WhenUserNotFound() {
        HabitDTO.CreateHabitRequest request = new HabitDTO.CreateHabitRequest();
        request.setName("Exercise");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> habitService.createHabit(999L, request));
        assertEquals("User not found", exception.getMessage());
        verify(habitRepository, never()).save(any());
    }

    @Test
    void getUserHabits_ShouldReturnListOfHabits() {
        when(habitRepository.findByUserId(1L)).thenReturn(List.of(testHabit));
        when(habitLogRepository.countCompletedBetween(anyLong(), any(), any())).thenReturn(5L);
        when(habitLogRepository.findByHabitIdAndLogDate(anyLong(), any())).thenReturn(Optional.empty());
        List<HabitDTO.HabitResponse> habits = habitService.getUserHabits(1L);
        assertNotNull(habits);
        assertEquals(1, habits.size());
        assertEquals("Exercise", habits.get(0).getName());
    }

    @Test
    void deleteHabit_ShouldThrowException_WhenHabitNotFound() {
        when(habitRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> habitService.deleteHabit(99L, 1L));
        assertEquals("Habit not found", exception.getMessage());
        verify(habitRepository, never()).delete(any());
    }

    @Test
    void calculateStreak_ShouldReturnZero_WhenNoLogsExist() {
        when(habitLogRepository.findByHabitIdAndLogDate(anyLong(), any())).thenReturn(Optional.empty());
        int streak = habitService.calculateStreak(1L);
        assertEquals(0, streak);
    }

    @Test
    void logHabit_ShouldCreateNewLog_WhenNoExistingLog() {
        HabitDTO.LogHabitRequest request = new HabitDTO.LogHabitRequest();
        request.setCompleted(true);
        request.setLogDate(LocalDate.now());
        HabitLog savedLog = new HabitLog();
        savedLog.setHabit(testHabit);
        savedLog.setLogDate(LocalDate.now());
        savedLog.setCompleted(true);
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testHabit));
        when(habitLogRepository.findByHabitIdAndLogDate(anyLong(), any())).thenReturn(Optional.empty());
        when(habitLogRepository.save(any(HabitLog.class))).thenReturn(savedLog);
        HabitLog result = habitService.logHabit(1L, 1L, request);
        assertNotNull(result);
        assertTrue(result.getCompleted());
        verify(habitLogRepository, times(1)).save(any(HabitLog.class));
    }
}
EOFcd ~/Downloads/healthtrackNew
git add .
git commit -m "add unit tests for HabitService"
git push
