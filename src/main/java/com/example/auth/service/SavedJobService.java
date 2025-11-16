package com.example.auth.service;

import com.example.auth.dto.JobPostSummary;
import com.example.auth.entity.*;
import com.example.auth.exception.CustomException;
import com.example.auth.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;
    private final JobSeekerProfileRepository seekerProfileRepository;
    private final JobPostRepository jobPostRepository;


    @Transactional
    public Map<String, String> toggleSavedJob(String email, Long jobPostId) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = seekerProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException("Job post not found", HttpStatus.NOT_FOUND));

        Optional<SavedJob> existing =
                savedJobRepository.findByJobSeekerProfileAndJobPost(profile, jobPost);

        if (existing.isPresent()) {
            savedJobRepository.delete(existing.get());
            return Map.of("status", "removed", "message", "Job removed from saved list");
        }

        SavedJob savedJob = SavedJob.builder()
                .jobSeekerProfile(profile)
                .jobPost(jobPost)
                .build();

        savedJobRepository.save(savedJob);

        return Map.of("status", "saved", "message", "Job saved successfully");
    }


    @Transactional(readOnly = true)
    public List<JobPostSummary> getRecentSavedJobs(String email) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = seekerProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        List<SavedJob> saved = savedJobRepository
                .findTop10ByJobSeekerProfileOrderBySavedAtDesc(profile);

        return saved.stream()
                .map(s -> convertToSummary(s.getJobPost()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobPostSummary> getAllSavedJobs(String email) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        JobSeekerProfile profile = seekerProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

        List<SavedJob> saved = savedJobRepository
                .findByJobSeekerProfileOrderBySavedAtDesc(profile);

        return saved.stream()
                .map(s -> convertToSummary(s.getJobPost()))
                .collect(Collectors.toList());
    }


    private JobPostSummary convertToSummary(JobPost job) {
        return JobPostSummary.builder()
                .id(job.getId())
                .title(job.getTitle())
                .companyName(job.getJobGiverProfile().getCompanyName())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .salaryCurrency(job.getSalaryCurrency())
                .experienceRequired(job.getExperienceRequired())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .build();
    }
}

