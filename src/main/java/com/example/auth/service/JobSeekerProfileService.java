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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public ProfileResponse createProfile(String email, CreateProfileRequest request,
                                         MultipartFile resume, MultipartFile profileImage) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (user.getRole() != Role.JOBSEEKER) {
            throw new CustomException("Only job seekers can create profiles", HttpStatus.FORBIDDEN);
        }

        if (profileRepository.existsByUser(user)) {
            throw new CustomException("Profile already exists for this user", HttpStatus.CONFLICT);
        }

        String resumeUrl = null;
        String profileImageUrl = null;

        if (resume != null && !resume.isEmpty()) {
            resumeUrl = cloudinaryService.uploadFile(resume, "resumes");
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = cloudinaryService.uploadFile(profileImage, "profiles");
        }

        JobSeekerProfile profile = JobSeekerProfile.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(user.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .bio(request.getBio())
                .resumeUrl(resumeUrl)
                .profileImageUrl(profileImageUrl)
                .languages(request.getLanguages() != null ? new ArrayList<>(request.getLanguages()) : new ArrayList<>())
                .skills(request.getSkills() != null ? new ArrayList<>(request.getSkills()) : new ArrayList<>())
                .educations(new ArrayList<>())
                .experiences(new ArrayList<>())
                .build();

        JobSeekerProfile savedProfile = profileRepository.save(profile);
        return mapToResponse(savedProfile);
    }

    @Transactional
    public ProfileResponse updateProfile(String email, UpdateProfileRequest request,
                                         MultipartFile resume, MultipartFile profileImage) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        if (request.getFirstName() != null) profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null) profile.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) profile.setPhoneNumber(request.getPhoneNumber());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getLanguages() != null) profile.setLanguages(new ArrayList<>(request.getLanguages()));
        if (request.getSkills() != null) profile.setSkills(new ArrayList<>(request.getSkills()));

        if (resume != null && !resume.isEmpty()) {
            String resumeUrl = cloudinaryService.uploadFile(resume, "resumes");
            profile.setResumeUrl(resumeUrl);
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImageUrl = cloudinaryService.uploadFile(profileImage, "profiles");
            profile.setProfileImageUrl(profileImageUrl);
        }

        JobSeekerProfile updatedProfile = profileRepository.save(profile);
        return mapToResponse(updatedProfile);
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

        profileRepository.delete(profile);
    }

    @Transactional
    public ProfileResponse addEducation(String email, EducationDTO dto) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        Education education = Education.builder()
                .profile(profile)
                .institution(dto.getInstitution())
                .degree(dto.getDegree())
                .fieldOfStudy(dto.getFieldOfStudy())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .currentlyStudying(dto.getCurrentlyStudying())
                .description(dto.getDescription())
                .grade(dto.getGrade())
                .build();

        profile.getEducations().add(education);
        JobSeekerProfile updatedProfile = profileRepository.save(profile);
        return mapToResponse(updatedProfile);
    }

    @Transactional
    public ProfileResponse updateEducation(String email, Long educationId, EducationDTO dto) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        Education education = profile.getEducations().stream()
                .filter(e -> e.getId().equals(educationId))
                .findFirst()
                .orElseThrow(() -> new CustomException("Education entry not found", HttpStatus.NOT_FOUND));

        education.setInstitution(dto.getInstitution());
        education.setDegree(dto.getDegree());
        education.setFieldOfStudy(dto.getFieldOfStudy());
        education.setStartDate(dto.getStartDate());
        education.setEndDate(dto.getEndDate());
        education.setCurrentlyStudying(dto.getCurrentlyStudying());
        education.setDescription(dto.getDescription());
        education.setGrade(dto.getGrade());

        JobSeekerProfile updatedProfile = profileRepository.save(profile);
        return mapToResponse(updatedProfile);
    }

    @Transactional
    public ProfileResponse deleteEducation(String email, Long educationId) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        boolean removed = profile.getEducations().removeIf(e -> e.getId().equals(educationId));

        if (!removed) {
            throw new CustomException("Education entry not found", HttpStatus.NOT_FOUND);
        }

        JobSeekerProfile updatedProfile = profileRepository.save(profile);
        return mapToResponse(updatedProfile);
    }

    @Transactional
    public ProfileResponse addExperience(String email, ExperienceDTO dto) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        Experience experience = Experience.builder()
                .profile(profile)
                .company(dto.getCompany())
                .position(dto.getPosition())
                .location(dto.getLocation())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .currentlyWorking(dto.getCurrentlyWorking())
                .description(dto.getDescription())
                .employmentType(dto.getEmploymentType())
                .build();

        profile.getExperiences().add(experience);
        JobSeekerProfile updatedProfile = profileRepository.save(profile);
        return mapToResponse(updatedProfile);
    }

    @Transactional
    public ProfileResponse updateExperience(String email, Long experienceId, ExperienceDTO dto) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        Experience experience = profile.getExperiences().stream()
                .filter(e -> e.getId().equals(experienceId))
                .findFirst()
                .orElseThrow(() -> new CustomException("Experience entry not found", HttpStatus.NOT_FOUND));

        experience.setCompany(dto.getCompany());
        experience.setPosition(dto.getPosition());
        experience.setLocation(dto.getLocation());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setCurrentlyWorking(dto.getCurrentlyWorking());
        experience.setDescription(dto.getDescription());
        experience.setEmploymentType(dto.getEmploymentType());

        JobSeekerProfile updatedProfile = profileRepository.save(profile);
        return mapToResponse(updatedProfile);
    }

    @Transactional
    public ProfileResponse deleteExperience(String email, Long experienceId) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        boolean removed = profile.getExperiences().removeIf(e -> e.getId().equals(experienceId));

        if (!removed) {
            throw new CustomException("Experience entry not found", HttpStatus.NOT_FOUND);
        }

        JobSeekerProfile updatedProfile = profileRepository.save(profile);
        return mapToResponse(updatedProfile);
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
                .languages(profile.getLanguages())
                .skills(profile.getSkills())
                .educations(profile.getEducations().stream()
                        .map(this::mapEducationToDTO)
                        .collect(Collectors.toList()))
                .experiences(profile.getExperiences().stream()
                        .map(this::mapExperienceToDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private EducationDTO mapEducationToDTO(Education education) {
        return EducationDTO.builder()
                .id(education.getId())
                .institution(education.getInstitution())
                .degree(education.getDegree())
                .fieldOfStudy(education.getFieldOfStudy())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .currentlyStudying(education.getCurrentlyStudying())
                .description(education.getDescription())
                .grade(education.getGrade())
                .build();
    }

    private ExperienceDTO mapExperienceToDTO(Experience experience) {
        return ExperienceDTO.builder()
                .id(experience.getId())
                .company(experience.getCompany())
                .position(experience.getPosition())
                .location(experience.getLocation())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .currentlyWorking(experience.getCurrentlyWorking())
                .description(experience.getDescription())
                .employmentType(experience.getEmploymentType())
                .build();
    }
}
