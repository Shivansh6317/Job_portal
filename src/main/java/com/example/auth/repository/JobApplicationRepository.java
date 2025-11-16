package com.example.auth.repository;

import com.example.auth.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByJobPostAndApplicant(JobPost jobPost, JobSeekerProfile applicant);
    Optional<JobApplication> findByJobPostAndApplicant(JobPost jobPost, JobSeekerProfile applicant);
    List<JobApplication> findByApplicantOrderByAppliedAtDesc(JobSeekerProfile applicant);

    Page<JobApplication> findByJobPost(JobPost jobPost, Pageable pageable);

    Page<JobApplication> findByJobPostAndStatus(JobPost jobPost, ApplicationStatus status, Pageable pageable);

    long countByJobPost(JobPost jobPost);
}
