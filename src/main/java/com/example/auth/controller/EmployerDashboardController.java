package com.example.auth.controller;

import com.example.auth.dto.EmployerDashboardResponse;
import com.example.auth.service.JobApplicationDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employer/dashboard")
@RequiredArgsConstructor
public class EmployerDashboardController {

    private final JobApplicationDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<EmployerDashboardResponse> getDashboard(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(dashboardService.getDashboard(email));
    }
}
