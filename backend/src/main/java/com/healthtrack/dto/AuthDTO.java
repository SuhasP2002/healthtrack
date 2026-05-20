package com.healthtrack.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDTO {

    @Data
    public static class RegisterRequest {
        @NotBlank private String name;
        @Email @NotBlank private String email;
        @NotBlank @Size(min = 6) private String password;
    }

    @Data
    public static class LoginRequest {
        @Email @NotBlank private String email;
        @NotBlank private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String name;
        private String email;
        private Long userId;

        public AuthResponse(String token, String name, String email, Long userId) {
            this.token = token;
            this.name = name;
            this.email = email;
            this.userId = userId;
        }
    }

    @Data
    public static class ForgotPasswordRequest {
        @Email @NotBlank private String email;
    }

    @Data
    public static class ResetPasswordRequest {
        @NotBlank private String token;
        @NotBlank @Size(min = 6) private String newPassword;
    }

    @Data
    public static class UpdateProfileRequest {
        private String name;
    }
}
