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
        request.setCategory("fitness");
        request.setFrequency("daily");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);
        when(habitLogRepository.countCompletedBetween(anyLong(), any(), any())).thenReturn(0L);
        when(habitLogRepository.findByHabitIdAndLogDate(anyLong(), any())).thenReturn(Optional.empty());
        HabitDTO.HabitResponse response = habitService.createHabit(1L, request);
        assertNotNull(response);
        assertEquals("Exercise", response.getName());
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    void createHabit_ShouldThrowException_WhenUserNotFound() {
        HabitDTO.CreateHabitRequest request = new HabitDTO.CreateHabitRequest();
        request.setName("Exercise");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> habitService.createHabit(999L, request));
        assertEquals("User not found", ex.getMessage());
        verify(habitRepository, never()).save(any());
    }

    @Test
    void calculateStreak_ShouldReturnZero_WhenNoLogsExist() {
        when(habitLogRepository.findByHabitIdAndLogDate(anyLong(), any())).thenReturn(Optional.empty());
        assertEquals(0, habitService.calculateStreak(1L));
    }
}
