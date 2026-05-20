package com.healthtrack.service;

import com.healthtrack.dto.AuthDTO;
import com.healthtrack.entity.PasswordResetToken;
import com.healthtrack.entity.User;
import com.healthtrack.repository.PasswordResetTokenRepository;
import com.healthtrack.repository.UserRepository;
import com.healthtrack.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private EmailService emailService;

    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already registered");
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);
        UserDetails ud = userDetailsService.loadUserByUsername(saved.getEmail());
        return new AuthDTO.AuthResponse(jwtUtil.generateToken(ud), saved.getName(), saved.getEmail(), saved.getId());
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDetails ud = userDetailsService.loadUserByUsername(user.getEmail());
        return new AuthDTO.AuthResponse(jwtUtil.generateToken(ud), user.getName(), user.getEmail(), user.getId());
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with that email"));
        tokenRepository.deleteByUserId(user.getId());
        String resetToken = UUID.randomUUID().toString();
        tokenRepository.save(new PasswordResetToken(resetToken, user));
        emailService.sendPasswordResetEmail(email, resetToken);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset link"));
        if (resetToken.isExpired()) throw new RuntimeException("Reset link has expired.");
        if (Boolean.TRUE.equals(resetToken.getUsed())) throw new RuntimeException("Reset link already used.");
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    public AuthDTO.AuthResponse updateProfile(Long userId, AuthDTO.UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getName() != null && !request.getName().isBlank())
            user.setName(request.getName());
        User saved = userRepository.save(user);
        UserDetails ud = userDetailsService.loadUserByUsername(saved.getEmail());
        return new AuthDTO.AuthResponse(jwtUtil.generateToken(ud), saved.getName(), saved.getEmail(), saved.getId());
    }
}
