package com.healthtrack.controller;

import com.healthtrack.entity.User;
import com.healthtrack.repository.UserRepository;
import com.healthtrack.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired private ExportService exportService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(Authentication auth) {
        try {
            User user = userRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            byte[] pdf = exportService.exportHabitsPdf(user.getId(), user.getName());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                ContentDisposition.attachment().filename("habit-report.pdf").build());
            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
