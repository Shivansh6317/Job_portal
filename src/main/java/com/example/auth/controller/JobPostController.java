package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.entity.JobType;
import com.example.auth.service.JobPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobPostController {

    private final JobPostService jobPostService;

    @PostMapping
    public ResponseEntity<JobPostResponse> createJobPost(
            Authentication authentication,
            @Valid @RequestBody CreateJobPostRequest request
    ) {
        String email = authentication.getName();
        JobPostResponse response = jobPostService.createJobPost(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{jobPostId}")
    public ResponseEntity<JobPostResponse> updateJobPost(
            Authentication authentication,
            @PathVariable Long jobPostId,
            @Valid @RequestBody UpdateJobPostRequest request
    ) {
        String email = authentication.getName();
        JobPostResponse response = jobPostService.updateJobPost(email, jobPostId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<List<JobPostResponse>> getMyJobPosts(Authentication authentication) {
        String email = authentication.getName();
        List<JobPostResponse> response = jobPostService.getMyJobPosts(email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{jobPostId}")
    public ResponseEntity<Map<String, String>> deleteJobPost(
            Authentication authentication,
            @PathVariable Long jobPostId
    ) {
        String email = authentication.getName();
        jobPostService.deleteJobPost(email, jobPostId);
        return ResponseEntity.ok(Map.of("message", "Job post deleted successfully"));
    }

    @GetMapping("/{jobPostId}")
    public ResponseEntity<JobPostResponse> getJobPostById(@PathVariable Long jobPostId) {
        JobPostResponse response = jobPostService.getJobPostById(jobPostId);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/search")
    public ResponseEntity<Page<JobPostSummary>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String companyName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<JobPostSummary> response = jobPostService.searchJobPostsDynamic(
                keyword,
                location,
                jobType,
                minSalary,
                maxSalary,
                skill,
                companyName,
                pageable
        );

        return ResponseEntity.ok(response);
    }
}





