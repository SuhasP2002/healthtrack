package com.healthtrack.controller;

import com.healthtrack.dto.AuthDTO;
import com.healthtrack.dto.HabitDTO;
import com.healthtrack.entity.User;
import com.healthtrack.repository.UserRepository;
import com.healthtrack.service.AuthService;
import com.healthtrack.service.HabitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private HabitService habitService;
    @Autowired private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        try { return ResponseEntity.ok(authService.register(request)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        try { return ResponseEntity.ok(authService.login(request)); }
        catch (Exception e) { return ResponseEntity.badRequest().body("Invalid email or password"); }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody AuthDTO.ForgotPasswordRequest request) {
        try { authService.forgotPassword(request.getEmail()); }
        catch (RuntimeException ignored) {}
        return ResponseEntity.ok("If that email is registered, you'll receive a reset link.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody AuthDTO.ResetPasswordRequest request) {
        try { authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully. You can now log in."); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        HabitDTO.UserStatsResponse stats = habitService.getUserStats(user.getId());
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("joinedDate", user.getCreatedAt());
        profile.put("stats", stats);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication auth,
                                           @RequestBody AuthDTO.UpdateProfileRequest request) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try { return ResponseEntity.ok(authService.updateProfile(user.getId(), request)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }
}
