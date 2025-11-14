package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.service.JobGiverProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/giver/profile")
@RequiredArgsConstructor
public class JobGiverProfileController {

    private final JobGiverProfileService profileService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<JobGiverProfileResponse> createProfile(
            @Valid @RequestPart("data") CreateJobGiverProfileRequest request,
            @RequestPart(value = "companyLogo", required = false) MultipartFile companyLogo,
            Authentication authentication
    ) {
        String email = authentication.getName();
        JobGiverProfileResponse response = profileService.createProfile(email, request, companyLogo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<JobGiverProfileResponse> updateProfile(
            @RequestPart(value = "data", required = false) UpdateJobGiverProfileRequest request,
            @RequestPart(value = "companyLogo", required = false) MultipartFile companyLogo,
            Authentication authentication
    ) {
        if (request == null) request = new UpdateJobGiverProfileRequest();
        String email = authentication.getName();
        JobGiverProfileResponse response = profileService.updateProfile(email, request, companyLogo);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<JobGiverProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        JobGiverProfileResponse response = profileService.getMyProfile(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobGiverProfileResponse> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteProfile(Authentication authentication) {
        String email = authentication.getName();
        profileService.deleteProfile(email);
        return ResponseEntity.ok(Map.of("message", "Profile deleted successfully"));
    }
}
