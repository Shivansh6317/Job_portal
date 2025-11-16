package com.example.auth.repository;

import com.example.auth.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByJobPostAndApplicant(JobPost jobPost, JobSeekerProfile applicant);
    Optional<JobApplication> findByJobPostAndApplicant(JobPost jobPost, JobSeekerProfile applicant);
    List<JobApplication> findByApplicantOrderByAppliedAtDesc(JobSeekerProfile applicant);

    Page<JobApplication> findByJobPost(JobPost jobPost, Pageable pageable);

    Page<JobApplication> findByJobPostAndStatus(JobPost jobPost, ApplicationStatus status, Pageable pageable);
    @Query("SELECT a.status, COUNT(a) FROM JobApplication a WHERE a.jobPost.jobGiverProfile.id = :profileId GROUP BY a.status")
    List<Object[]> countApplicationsGroupedByStatus(Long profileId);

    @Query("SELECT a.jobPost.id, a.jobPost.title, a.jobPost.status, COUNT(a) " +
            "FROM JobApplication a " +
            "WHERE a.jobPost.jobGiverProfile.id = :profileId " +
            "GROUP BY a.jobPost.id, a.jobPost.title, a.jobPost.status")
    List<Object[]> countApplicationsPerJob(Long profileId);
    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.jobPost.jobGiverProfile.id = :profileId")
    long countApplicationsPerProfile(Long profileId);
    long countByProfile(Long profileId);

    @Query("SELECT a.status, COUNT(a) FROM JobApplication a WHERE a.profile.id = :profileId GROUP BY a.status")
    List<Object[]> countByStatusGroup(Long profileId);

    List<JobApplication> findTop5ByProfileOrderByAppliedAtDesc(JobSeekerProfile profile);

    long countByJobPost(JobPost jobPost);
}
