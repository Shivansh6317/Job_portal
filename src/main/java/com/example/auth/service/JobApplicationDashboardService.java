package com.example.auth.service;

import com.example.auth.dto.EmployerDashboardResponse;
import com.example.auth.dto.*;
import com.example.auth.entity.*;
import com.example.auth.exception.CustomException;
import com.example.auth.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationDashboardService {

    private final UserRepository userRepository;
    private final JobGiverProfileRepository jobGiverProfileRepository;
    private final JobPostRepository jobPostRepository;
    private final JobApplicationRepository jobApplicationRepository;


    public EmployerDashboardResponse getDashboard(String email) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobGiverProfile profile = jobGiverProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Job giver profile not found", HttpStatus.NOT_FOUND));

        // Counts of job posts
        long totalJobs = jobPostRepository.countByJobGiverProfile(profile);
        long active = jobPostRepository.countByJobGiverProfileAndStatus(profile, JobStatus.ACTIVE);
        long closed = jobPostRepository.countByJobGiverProfileAndStatus(profile, JobStatus.CLOSED);
        long draft = jobPostRepository.countByJobGiverProfileAndStatus(profile, JobStatus.DRAFT);

        long totalApplications = jobApplicationRepository.countApplicationsPerProfile(profile.getId());

        List<ApplicationStatusCount> groupedStatus = jobApplicationRepository
                .countApplicationsGroupedByStatus(profile.getId())
                .stream()
                .map(row -> ApplicationStatusCount.builder()
                        .status((ApplicationStatus) row[0])
                        .count((Long) row[1])
                        .build()
                ).toList();

        List<JobApplicationStats> jobWiseStats = jobApplicationRepository
                .countApplicationsPerJob(profile.getId())
                .stream()
                .map(row -> JobApplicationStats.builder()
                        .jobPostId((Long) row[0])
                        .title((String) row[1])
                        .status((JobStatus) row[2])
                        .applicants((Long) row[3])
                        .build()
                ).toList();

        return EmployerDashboardResponse.builder()
                .totalJobPosts(totalJobs)
                .activeJobs(active)
                .closedJobs(closed)
                .draftJobs(draft)
                .totalApplications(totalApplications)
                .applicationStatusCounts(groupedStatus)
                .jobWiseStats(jobWiseStats)
                .build();
    }
}
