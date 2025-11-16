package com.example.auth.service;

import com.example.auth.dto.JobPostSummary;
import com.example.auth.dto.JobSeekerDashboardResponse;
import com.example.auth.entity.JobSeekerProfile;
import com.example.auth.entity.User;
import com.example.auth.exception.CustomException;
import com.example.auth.repository.JobApplicationRepository;
import com.example.auth.repository.SavedJobRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSeekerDashboardService {

    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final JobPostService jobPostService;

    public JobSeekerDashboardResponse getDashboard(String email) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile seekerProfile = user.getJobSeekerProfile();
        if (seekerProfile == null)
            throw new CustomException("Create your profile to start applying for jobs", HttpStatus.BAD_REQUEST);


        Long profileId = seekerProfile.getId();


        long totalApplications = jobApplicationRepository.countByProfile(profileId);


        Map<String, Long> statusCounts = jobApplicationRepository.countByStatusGroup(profileId)
                .stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1]
                ));


        long savedJobs = savedJobRepository.countByJobSeekerProfile(seekerProfile);


        var recentApplied = jobApplicationRepository.findTop5ByProfileOrderByAppliedAtDesc(seekerProfile)
                .stream()
                .map(a -> jobPostService.getJobPostById(a.getJobPost().getId()))
                .map(this::convertToSummary)
                .toList();


        var recentSaved = savedJobRepository.findTop5ByJobSeekerProfileOrderByIdDesc(seekerProfile)
                .stream()
                .map(s -> jobPostService.getJobPostById(s.getJobPost().getId()))
                .map(this::convertToSummary)
                .toList();


        return JobSeekerDashboardResponse.builder()
                .totalApplications(totalApplications)
                .applicationStatusBreakdown(statusCounts)
                .savedJobs(savedJobs)
                .recentAppliedJobs(recentApplied)
                .recentSavedJobs(recentSaved)
                .build();
    }

    private JobPostSummary convertToSummary(com.example.auth.dto.JobPostResponse r) {
        return JobPostSummary.builder()
                .id(r.getId())
                .title(r.getTitle())
                .companyName(r.getCompanyName())
                .location(r.getLocation())
                .jobType(r.getJobType())
                .minSalary(r.getMinSalary())
                .maxSalary(r.getMaxSalary())
                .salaryCurrency(r.getSalaryCurrency())
                .experienceRequired(r.getExperienceRequired())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
