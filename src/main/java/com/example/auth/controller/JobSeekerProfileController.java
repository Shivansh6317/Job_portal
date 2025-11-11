package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.service.JobSeekerProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/jobseeker/profile")
@RequiredArgsConstructor
public class JobSeekerProfileController {

    private final JobSeekerProfileService profileService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(
            Authentication authentication,
            @RequestPart("profile") String profileJson,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        try {
            String email = authentication.getName();
            CreateProfileRequest request = objectMapper.readValue(profileJson, CreateProfileRequest.class);
            ProfileResponse response = profileService.createProfile(email, request, resume, profileImage);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse profile data: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            Authentication authentication,
            @RequestPart("profile") String profileJson,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        try {
            String email = authentication.getName();
            UpdateProfileRequest request = objectMapper.readValue(profileJson, UpdateProfileRequest.class);
            ProfileResponse response = profileService.updateProfile(email, request, resume, profileImage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse profile data: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        ProfileResponse response = profileService.getMyProfile(email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteProfile(Authentication authentication) {
        String email = authentication.getName();
        profileService.deleteProfile(email);
        return ResponseEntity.ok(Map.of("message", "Profile deleted successfully"));
    }

    @PostMapping("/education")
    public ResponseEntity<ProfileResponse> addEducation(
            Authentication authentication,
            @Valid @RequestBody EducationDTO educationDTO
    ) {
        String email = authentication.getName();
        ProfileResponse response = profileService.addEducation(email, educationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/education/{educationId}")
    public ResponseEntity<ProfileResponse> updateEducation(
            Authentication authentication,
            @PathVariable Long educationId,
            @Valid @RequestBody EducationDTO educationDTO
    ) {
        String email = authentication.getName();
        ProfileResponse response = profileService.updateEducation(email, educationId, educationDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/education/{educationId}")
    public ResponseEntity<ProfileResponse> deleteEducation(
            Authentication authentication,
            @PathVariable Long educationId
    ) {
        String email = authentication.getName();
        ProfileResponse response = profileService.deleteEducation(email, educationId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/experience")
    public ResponseEntity<ProfileResponse> addExperience(
            Authentication authentication,
            @Valid @RequestBody ExperienceDTO experienceDTO
    ) {
        String email = authentication.getName();
        ProfileResponse response = profileService.addExperience(email, experienceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/experience/{experienceId}")
    public ResponseEntity<ProfileResponse> updateExperience(
            Authentication authentication,
            @PathVariable Long experienceId,
            @Valid @RequestBody ExperienceDTO experienceDTO
    ) {
        String email = authentication.getName();
        ProfileResponse response = profileService.updateExperience(email, experienceId, experienceDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/experience/{experienceId}")
    public ResponseEntity<ProfileResponse> deleteExperience(
            Authentication authentication,
            @PathVariable Long experienceId
    ) {
        String email = authentication.getName();
        ProfileResponse response = profileService.deleteExperience(email, experienceId);
        return ResponseEntity.ok(response);
    }
}