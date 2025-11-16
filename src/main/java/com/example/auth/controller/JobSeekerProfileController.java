package com.example.auth.controller;
import com.example.auth.dto.*;
import com.example.auth.exception.CustomException;
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
@RequestMapping("/api/job-seeker/profile")
@RequiredArgsConstructor
public class JobSeekerProfileController {

    private final JobSeekerProfileService profileService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ProfileResponse> createProfile(
            @Valid @RequestPart("data") CreateProfileRequest request,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "additionalFile", required = false) MultipartFile additionalFile,

            Authentication authentication
    ){
        String email = authentication.getName();
        ProfileResponse response = profileService.createProfile(email, request, resume, profileImage,additionalFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestPart(value = "data", required = false) String rawRequest,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "additionalFile", required = false) MultipartFile additionalFile,
            Authentication authentication
    ) {
        UpdateProfileRequest request;

        try {
            if (rawRequest != null && !rawRequest.isBlank()) {
                ObjectMapper mapper = new ObjectMapper();
                request = mapper.readValue(rawRequest, UpdateProfileRequest.class);
            } else {
                request = new UpdateProfileRequest();
            }
        } catch (Exception e) {
            throw new CustomException("Invalid JSON format in 'data' field", HttpStatus.BAD_REQUEST);
        }

        String email = authentication.getName();
        ProfileResponse response = profileService.updateProfile(
                email,
                request,
                resume,
                profileImage,
                additionalFile
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
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
}