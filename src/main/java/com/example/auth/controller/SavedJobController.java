package com.example.auth.controller;
import com.example.auth.dto.JobPostSummary;
import com.example.auth.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;


    @PutMapping("/toggle/{jobPostId}")
    public ResponseEntity<Map<String, String>> toggleSavedJob(
            Authentication auth,
            @PathVariable Long jobPostId
    ) {
        return ResponseEntity.ok(
                savedJobService.toggleSavedJob(auth.getName(), jobPostId)
        );
    }


    @GetMapping("/recent")
    public ResponseEntity<List<JobPostSummary>> getRecentSavedJobs(
            Authentication auth
    ) {
        return ResponseEntity.ok(
                savedJobService.getRecentSavedJobs(auth.getName())
        );
    }


    @GetMapping
    public ResponseEntity<List<JobPostSummary>> getAllSavedJobs(
            Authentication auth
    ) {
        return ResponseEntity.ok(
                savedJobService.getAllSavedJobs(auth.getName())
        );
    }
}
