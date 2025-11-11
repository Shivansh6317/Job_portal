package com.example.auth.service;

import com.example.auth.dto.*;
import com.example.auth.entity.*;
import com.example.auth.exception.CustomException;
import com.example.auth.repository.JobGiverProfileRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class JobGiverProfileService {

    private final JobGiverProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public JobGiverProfileResponse createProfile(String email, CreateJobGiverProfileRequest request,
                                                 MultipartFile companyLogo) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (user.getRole() != Role.JOBGIVER) {
            throw new CustomException("Only job givers can create company profiles", HttpStatus.FORBIDDEN);
        }

        if (profileRepository.existsByUser(user)) {
            throw new CustomException("Profile already exists for this user", HttpStatus.CONFLICT);
        }

        String companyLogoUrl = null;
        if (companyLogo != null && !companyLogo.isEmpty()) {
            companyLogoUrl = cloudinaryService.uploadFile(companyLogo, "company-logos");
        }

        JobGiverProfile profile = JobGiverProfile.builder()
                .user(user)
                .fullName(request.getFullName())
                .jobTitle(request.getJobTitle())
                .companyName(request.getCompanyName())
                .location(request.getLocation())
                .contact(request.getContact())
                .companyLogoUrl(companyLogoUrl)
                .bio(request.getBio())
                .companyDescription(request.getCompanyDescription())
                .linkedinProfileUrl(request.getLinkedinProfileUrl())
                .specializations(request.getSpecializations() != null ?
                        new ArrayList<>(request.getSpecializations()) : new ArrayList<>())
                .jobPosts(new ArrayList<>())
                .build();

        JobGiverProfile savedProfile = profileRepository.save(profile);
        return mapToResponse(savedProfile);
    }

    @Transactional
    public JobGiverProfileResponse updateProfile(String email, UpdateJobGiverProfileRequest request,
                                                 MultipartFile companyLogo) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobGiverProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getJobTitle() != null) profile.setJobTitle(request.getJobTitle());
        if (request.getCompanyName() != null) profile.setCompanyName(request.getCompanyName());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getContact() != null) profile.setContact(request.getContact());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getCompanyDescription() != null) profile.setCompanyDescription(request.getCompanyDescription());
        if (request.getLinkedinProfileUrl() != null) profile.setLinkedinProfileUrl(request.getLinkedinProfileUrl());
        if (request.getSpecializations() != null) profile.setSpecializations(new ArrayList<>(request.getSpecializations()));

        if (companyLogo != null && !companyLogo.isEmpty()) {
            String companyLogoUrl = cloudinaryService.uploadFile(companyLogo, "company-logos");
            profile.setCompanyLogoUrl(companyLogoUrl);
        }

        JobGiverProfile updatedProfile = profileRepository.save(profile);
        return mapToResponse(updatedProfile);
    }

    @Transactional(readOnly = true)
    public JobGiverProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobGiverProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found. Please create your company profile first.",
                        HttpStatus.NOT_FOUND));

        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public JobGiverProfileResponse getProfileById(Long profileId) {
        JobGiverProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        return mapToResponse(profile);
    }

    @Transactional
    public void deleteProfile(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobGiverProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        profileRepository.delete(profile);
    }

    private JobGiverProfileResponse mapToResponse(JobGiverProfile profile) {
        return JobGiverProfileResponse.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .jobTitle(profile.getJobTitle())
                .companyName(profile.getCompanyName())
                .location(profile.getLocation())
                .contact(profile.getContact())
                .companyLogoUrl(profile.getCompanyLogoUrl())
                .bio(profile.getBio())
                .companyDescription(profile.getCompanyDescription())
                .linkedinProfileUrl(profile.getLinkedinProfileUrl())
                .specializations(profile.getSpecializations())
                .totalJobPosts(profile.getJobPosts() != null ? profile.getJobPosts().size() : 0)
                .build();
    }
}