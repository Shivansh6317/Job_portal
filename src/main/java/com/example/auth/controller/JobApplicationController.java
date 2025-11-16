package com.example.auth.controller;
import com.example.auth.dto.JobApplicationResponse;
import com.example.auth.dto.UpdateApplicationStatusRequest;
import com.example.auth.entity.ApplicationStatus;
import com.example.auth.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService applicationService;

    @PostMapping("/job/{jobPostId}")
    public ResponseEntity<JobApplicationResponse> applyToJob(
            Authentication authentication,
            @PathVariable Long jobPostId
    ) {
        String email = authentication.getName();
        JobApplicationResponse response = applicationService.applyToJob(email, jobPostId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/my")
    public ResponseEntity<List<JobApplicationResponse>> getMyApplications(
            Authentication authentication
    ) {
        String email = authentication.getName();
        List<JobApplicationResponse> response = applicationService.getMyApplications(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/job/{jobPostId}")
    public ResponseEntity<Page<JobApplicationResponse>> getApplicationsForJob(
            Authentication authentication,
            @PathVariable Long jobPostId,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = authentication.getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<JobApplicationResponse> response =
                applicationService.getApplicationsForJob(email, jobPostId, status, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
            Authentication authentication,
            @PathVariable Long applicationId,
            @RequestBody UpdateApplicationStatusRequest request
    ) {
        String email = authentication.getName();
        JobApplicationResponse response =
                applicationService.updateApplicationStatus(email, applicationId, request.getStatus());
        return ResponseEntity.ok(response);
    }
}
