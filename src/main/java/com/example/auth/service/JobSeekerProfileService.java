package com.example.auth.service;

import com.example.auth.dto.*;
import com.example.auth.entity.*;
import com.example.auth.exception.CustomException;
import com.example.auth.repository.JobSeekerProfileRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public ProfileResponse createProfile(String email, CreateProfileRequest request,
                                         MultipartFile resume, MultipartFile profileImage,MultipartFile additionalFile) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (profileRepository.existsByUser(user)) {
            throw new CustomException("Profile already exists for this user", HttpStatus.CONFLICT);
        }

        String resumeUrl = (resume != null && !resume.isEmpty())
                ? cloudinaryService.uploadFile(resume, "resumes")
                : null;

        String profileImageUrl = (profileImage != null && !profileImage.isEmpty())
                ? cloudinaryService.uploadFile(profileImage, "profiles")
                : null;
        String additionalFileUrl = (additionalFile != null && !additionalFile.isEmpty())
                ? cloudinaryService.uploadFile(additionalFile, "additional_files")
                : null;


        JobSeekerProfile profile = JobSeekerProfile.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(user.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .bio(request.getBio())
                .resumeUrl(resumeUrl)
                .profileImageUrl(profileImageUrl)
                .additionalFileUrl(additionalFileUrl)
                .languages(request.getLanguages() != null ? new ArrayList<>(request.getLanguages()) : new ArrayList<>())
                .skills(request.getSkills() != null ? new ArrayList<>(request.getSkills()) : new ArrayList<>())
                .educations(request.getEducations() != null ? new ArrayList<>(request.getEducations()) : new ArrayList<>())
                .experiences(request.getExperiences() != null ? new ArrayList<>(request.getExperiences()) : new ArrayList<>())
                .build();
        profileRepository.save(profile);
        user.setJobSeekerProfile(profile);

        userRepository.save(user);
        return mapToResponse(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(String email, UpdateProfileRequest request,
                                         MultipartFile resume, MultipartFile profileImage,MultipartFile additionalFile) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        if (request.getFirstName() != null)
            profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            profile.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null)
            profile.setPhoneNumber(request.getPhoneNumber());
        if (request.getBio() != null)
            profile.setBio(request.getBio());
        if (request.getLanguages() != null)
            profile.setLanguages(new ArrayList<>(request.getLanguages()));
        if (request.getSkills() != null)
            profile.setSkills(new ArrayList<>(request.getSkills()));
        if (request.getEducations() != null)
            profile.setEducations(new ArrayList<>(request.getEducations()));

        if (request.getExperiences() != null)
            profile.setExperiences(new ArrayList<>(request.getExperiences()));

        if (resume != null && !resume.isEmpty()) {
            String resumeUrl = cloudinaryService.uploadFile(resume, "resumes");
            profile.setResumeUrl(resumeUrl);
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImageUrl = cloudinaryService.uploadFile(profileImage, "profiles");
            profile.setProfileImageUrl(profileImageUrl);
        }
        if (additionalFile != null && !additionalFile.isEmpty()) {
            String uploadedUrl = cloudinaryService.uploadFile(additionalFile, "additional_files");
            profile.setAdditionalFileUrl(uploadedUrl);
        }


        profileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found. Please create your profile first.", HttpStatus.NOT_FOUND));

        return mapToResponse(profile);
    }

    @Transactional
    public void deleteProfile(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));
        user.setJobSeekerProfile(null);
        userRepository.save(user);
        profileRepository.delete(profile);
    }

    private ProfileResponse mapToResponse(JobSeekerProfile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .phoneNumber(profile.getPhoneNumber())
                .bio(profile.getBio())
                .resumeUrl(profile.getResumeUrl())
                .profileImageUrl(profile.getProfileImageUrl())
                .additionalFileUrl(profile.getAdditionalFileUrl())
                .languages(profile.getLanguages())
                .skills(profile.getSkills())
                .educations(profile.getEducations())
                .experiences(profile.getExperiences())
                .build();
    }


}
