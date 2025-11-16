package com.example.auth.service;

import com.example.auth.dto.JobApplicationResponse;
import com.example.auth.dto.UpdateApplicationStatusRequest;
import com.example.auth.entity.*;
import com.example.auth.exception.CustomException;
import com.example.auth.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobPostRepository jobPostRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final JobGiverProfileRepository jobGiverProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public JobApplicationResponse applyToJob(String email, Long jobPostId) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = jobSeekerProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Please create your job seeker profile first",
                        HttpStatus.BAD_REQUEST));

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException("Job post not found", HttpStatus.NOT_FOUND));

        switch (jobPost.getStatus()) {
            case DRAFT -> throw new CustomException("This job is not published yet.", HttpStatus.BAD_REQUEST);
            case CLOSED -> throw new CustomException("This job is closed and no longer accepting applications.", HttpStatus.BAD_REQUEST);
            case ACTIVE -> {}
            default -> throw new CustomException("This job is not accepting applications.", HttpStatus.BAD_REQUEST);
        }


        if (jobPost.getApplicationDeadline() != null &&
                jobPost.getApplicationDeadline().isBefore(java.time.LocalDateTime.now())) {
            throw new CustomException("Application deadline has passed", HttpStatus.BAD_REQUEST);
        }

        if (applicationRepository.existsByJobPostAndApplicant(jobPost, profile)) {
            throw new CustomException("You have already applied to this job", HttpStatus.CONFLICT);
        }

        JobApplication application = JobApplication.builder()
                .jobPost(jobPost)
                .applicant(profile)
                .status(ApplicationStatus.SENT)
                .build();

        return mapToResponse(applicationRepository.save(application));
    }
    @Transactional
    public JobApplicationResponse withdrawApplication(String email, Long applicationId) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = jobSeekerProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Job seeker profile not found", HttpStatus.NOT_FOUND));

        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException("Application not found", HttpStatus.NOT_FOUND));

        if (!application.getApplicant().getId().equals(profile.getId())) {
            throw new CustomException("You are not allowed to withdraw this application", HttpStatus.FORBIDDEN);
        }

        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new CustomException("Application is already withdrawn", HttpStatus.BAD_REQUEST);
        }

        if (application.getStatus() == ApplicationStatus.INTERVIEW ||application.getStatus() == ApplicationStatus.OFFERED ||
                application.getStatus() == ApplicationStatus.REJECTED) {
            throw new CustomException("You cannot withdraw an already reviewed or offered application", HttpStatus.BAD_REQUEST);
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        JobApplication updated = applicationRepository.save(application);

        return mapToResponse(updated);
    }


    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getMyApplications(String email) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = jobSeekerProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Job seeker profile not found", HttpStatus.NOT_FOUND));

        return applicationRepository.findByApplicantOrderByAppliedAtDesc(profile)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getApplicationsForJob(
            String employerEmail,
            Long jobPostId,
            ApplicationStatus status,
            Pageable pageable
    ) {
        User user = userRepository.findByEmail(employerEmail.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobGiverProfile profile = jobGiverProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Job giver profile not found", HttpStatus.NOT_FOUND));

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException("Job post not found", HttpStatus.NOT_FOUND));

        if (!jobPost.getJobGiverProfile().getId().equals(profile.getId())) {
            throw new CustomException("You are not allowed to view applications for this job",
                    HttpStatus.FORBIDDEN);
        }

        Page<JobApplication> page = (status == null)
                ? applicationRepository.findByJobPost(jobPost, pageable)
                : applicationRepository.findByJobPostAndStatus(jobPost, status, pageable);

        return page.map(this::mapToResponse);
    }

    @Transactional
    public JobApplicationResponse updateApplicationStatus(
            String employerEmail,
            Long applicationId,
            ApplicationStatus newStatus
    ) {
        if (newStatus == null) {
            throw new CustomException("Status is required", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(employerEmail.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobGiverProfile profile = jobGiverProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Job giver profile not found", HttpStatus.NOT_FOUND));

        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException("Application not found", HttpStatus.NOT_FOUND));

        if (!application.getJobPost().getJobGiverProfile().getId().equals(profile.getId())) {
            throw new CustomException("You are not allowed to update this application",
                    HttpStatus.FORBIDDEN);
        }

        application.setStatus(newStatus);
        JobApplication updated = applicationRepository.save(application);
        return mapToResponse(updated);
    }

    private JobApplicationResponse mapToResponse(JobApplication application) {
        JobPost job = application.getJobPost();
        JobGiverProfile company = job.getJobGiverProfile();
        JobSeekerProfile seeker = application.getApplicant();

        return JobApplicationResponse.builder()
                .id(application.getId())
                .jobPostId(job.getId())
                .jobTitle(job.getTitle())
                .companyName(company.getCompanyName())
                .companyLocation(company.getLocation())
                .jobType(job.getJobType())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getUpdatedAt())
                .applicantId(seeker.getId())
                .applicantName(seeker.getFirstName() + " " + seeker.getLastName())
                .applicantEmail(seeker.getEmail())
                .resumeUrl(seeker.getResumeUrl())
                .additionalFileUrl(seeker.getAdditionalFileUrl())
                .build();
    }
}
