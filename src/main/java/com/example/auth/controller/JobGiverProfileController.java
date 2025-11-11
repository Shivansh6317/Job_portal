package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.service.JobGiverProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/creator/profile")
@RequiredArgsConstructor
public class JobGiverProfileController {

    private final JobGiverProfileService profileService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<JobGiverProfileResponse> createProfile(
            Authentication authentication,
            @RequestPart("profile") String profileJson,
            @RequestPart(value = "companyLogo", required = false) MultipartFile companyLogo
    ) {
        try {
            String email = authentication.getName();
            CreateJobGiverProfileRequest request = objectMapper.readValue(profileJson, CreateJobGiverProfileRequest.class);
            JobGiverProfileResponse response = profileService.createProfile(email, request, companyLogo);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse profile data: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<JobGiverProfileResponse> updateProfile(
            Authentication authentication,
            @RequestPart("profile") String profileJson,
            @RequestPart(value = "companyLogo", required = false) MultipartFile companyLogo
    ) {
        try {
            String email = authentication.getName();
            UpdateJobGiverProfileRequest request = objectMapper.readValue(profileJson, UpdateJobGiverProfileRequest.class);
            JobGiverProfileResponse response = profileService.updateProfile(email, request, companyLogo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse profile data: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<JobGiverProfileResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        JobGiverProfileResponse response = profileService.getMyProfile(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<JobGiverProfileResponse> getProfileById(@PathVariable Long profileId) {
        JobGiverProfileResponse response = profileService.getProfileById(profileId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteProfile(Authentication authentication) {
        String email = authentication.getName();
        profileService.deleteProfile(email);
        return ResponseEntity.ok(Map.of("message", "Company profile deleted successfully"));
    }
}
