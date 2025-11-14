package com.example.auth.service;

import com.example.auth.dto.*;
import com.example.auth.entity.*;
import com.example.auth.exception.CustomException;
import com.example.auth.repository.JobGiverProfileRepository;
import com.example.auth.repository.JobPostRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostService {

    private final JobPostRepository jobPostRepository;
    private final JobGiverProfileRepository profileRepository;
    private final UserRepository userRepository;


    @Transactional
    public JobPostResponse createJobPost(String email, CreateJobPostRequest request) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobGiverProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(
                        "Please create your company profile first",
                        HttpStatus.BAD_REQUEST
                ));


        if (request.getMinSalary() != null && request.getMaxSalary() != null &&
                request.getMinSalary().compareTo(request.getMaxSalary()) > 0) {
            throw new CustomException("Minimum salary cannot be greater than maximum salary",
                    HttpStatus.BAD_REQUEST);
        }

        JobPost jobPost = JobPost.builder()
                .jobGiverProfile(profile)
                .title(request.getTitle())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .minSalary(request.getMinSalary())
                .maxSalary(request.getMaxSalary())
                .salaryCurrency(request.getSalaryCurrency() != null ? request.getSalaryCurrency() : "INR")
                .description(request.getDescription())
                .responsibilities(request.getResponsibilities())
                .qualifications(request.getQualifications())
                .experienceRequired(request.getExperienceRequired())
                .skills(request.getSkills() != null ? new ArrayList<>(request.getSkills()) : new ArrayList<>())
                .status(JobStatus.ACTIVE)
                .applicationDeadline(request.getApplicationDeadline())
                .build();

        return mapToResponse(jobPostRepository.save(jobPost));
    }


    @Transactional
    public JobPostResponse updateJobPost(String email, Long jobPostId, UpdateJobPostRequest request) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobGiverProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException("Job post not found", HttpStatus.NOT_FOUND));


        if (!jobPost.getJobGiverProfile().getId().equals(profile.getId())) {
            throw new CustomException("You are not allowed to update this job post",
                    HttpStatus.FORBIDDEN);
        }

        if (request.getTitle() != null) jobPost.setTitle(request.getTitle());
        if (request.getLocation() != null) jobPost.setLocation(request.getLocation());
        if (request.getJobType() != null) jobPost.setJobType(request.getJobType());
        if (request.getMinSalary() != null) jobPost.setMinSalary(request.getMinSalary());
        if (request.getMaxSalary() != null) jobPost.setMaxSalary(request.getMaxSalary());
        if (request.getSalaryCurrency() != null) jobPost.setSalaryCurrency(request.getSalaryCurrency());
        if (request.getDescription() != null) jobPost.setDescription(request.getDescription());
        if (request.getResponsibilities() != null) jobPost.setResponsibilities(request.getResponsibilities());
        if (request.getQualifications() != null) jobPost.setQualifications(request.getQualifications());
        if (request.getExperienceRequired() != null) jobPost.setExperienceRequired(request.getExperienceRequired());
        if (request.getSkills() != null) jobPost.setSkills(new ArrayList<>(request.getSkills()));
        if (request.getStatus() != null) jobPost.setStatus(request.getStatus());
        if (request.getApplicationDeadline() != null)
            jobPost.setApplicationDeadline(request.getApplicationDeadline());


        if (jobPost.getMinSalary() != null && jobPost.getMaxSalary() != null &&
                jobPost.getMinSalary().compareTo(jobPost.getMaxSalary()) > 0) {
            throw new CustomException("Minimum salary cannot be greater than maximum salary",
                    HttpStatus.BAD_REQUEST);
        }

        return mapToResponse(jobPostRepository.save(jobPost));
    }


    @Transactional(readOnly = true)
    public JobPostResponse getJobPostById(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException("Job post not found", HttpStatus.NOT_FOUND));

        return mapToResponse(jobPost);
    }


    @Transactional(readOnly = true)
    public List<JobPostResponse> getMyJobPosts(String email) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobGiverProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        return jobPostRepository.findByJobGiverProfile(profile)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public void deleteJobPost(String email, Long jobPostId) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobGiverProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException("Job post not found", HttpStatus.NOT_FOUND));

        if (!jobPost.getJobGiverProfile().getId().equals(profile.getId())) {
            throw new CustomException("You are not allowed to delete this job post",
                    HttpStatus.FORBIDDEN);
        }

        jobPostRepository.delete(jobPost);
    }


    @Transactional(readOnly = true)
    public Page<JobPostSummary> searchJobPostsDynamic(
            String keyword,
            String location,
            JobType jobType,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            String skill,
            String companyName,
            Pageable pageable
    ) {

        Specification<JobPost> spec = Specification.allOf(
                JobPostSpecification.hasStatus(JobStatus.ACTIVE),
                JobPostSpecification.keyword(keyword),
                JobPostSpecification.hasLocation(location),
                JobPostSpecification.hasJobType(jobType),
                JobPostSpecification.minSalary(minSalary),
                JobPostSpecification.maxSalary(maxSalary),
                JobPostSpecification.hasSkill(skill),
                JobPostSpecification.hasCompanyName(companyName)
        );


        return jobPostRepository.findAll(spec, pageable)
                .map(this::mapToSummary);
    }


    private JobPostResponse mapToResponse(JobPost jobPost) {
        return JobPostResponse.builder()
                .id(jobPost.getId())
                .title(jobPost.getTitle())
                .location(jobPost.getLocation())
                .jobType(jobPost.getJobType())
                .minSalary(jobPost.getMinSalary())
                .maxSalary(jobPost.getMaxSalary())
                .salaryCurrency(jobPost.getSalaryCurrency())
                .description(jobPost.getDescription())
                .responsibilities(jobPost.getResponsibilities())
                .qualifications(jobPost.getQualifications())
                .experienceRequired(jobPost.getExperienceRequired())
                .skills(jobPost.getSkills())
                .status(jobPost.getStatus())
                .applicationDeadline(jobPost.getApplicationDeadline())
                .createdAt(jobPost.getCreatedAt())
                .updatedAt(jobPost.getUpdatedAt())


                .companyName(jobPost.getJobGiverProfile().getCompanyName())
                .companyLogoUrl(jobPost.getJobGiverProfile().getCompanyLogoUrl())
                .companyLocation(jobPost.getJobGiverProfile().getLocation())

                .build();
    }

    private JobPostSummary mapToSummary(JobPost jobPost) {
        return JobPostSummary.builder()
                .id(jobPost.getId())
                .title(jobPost.getTitle())
                .companyName(jobPost.getJobGiverProfile().getCompanyName())
                .location(jobPost.getLocation())
                .jobType(jobPost.getJobType())
                .minSalary(jobPost.getMinSalary())
                .maxSalary(jobPost.getMaxSalary())
                .salaryCurrency(jobPost.getSalaryCurrency())
                .experienceRequired(jobPost.getExperienceRequired())
                .status(jobPost.getStatus())
                .createdAt(jobPost.getCreatedAt())
                .build();
    }
}
