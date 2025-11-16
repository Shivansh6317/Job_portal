package com.example.auth.controller;

import com.example.auth.dto.JobSeekerDashboardResponse;
import com.example.auth.service.JobSeekerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobseeker/dashboard")
@RequiredArgsConstructor
public class JobSeekerDashboardController {

    private final JobSeekerDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<JobSeekerDashboardResponse> getDashboard(Authentication auth) {
        return ResponseEntity.ok(dashboardService.getDashboard(auth.getName()));
    }
}
