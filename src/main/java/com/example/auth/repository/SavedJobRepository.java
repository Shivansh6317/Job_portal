package com.example.auth.repository;

import com.example.auth.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    Optional<SavedJob> findByJobSeekerProfileAndJobPost(
            JobSeekerProfile profile,
            JobPost jobPost
    );

    boolean existsByJobSeekerProfileAndJobPost(
            JobSeekerProfile profile,
            JobPost jobPost
    );

    List<SavedJob> findTop10ByJobSeekerProfileOrderBySavedAtDesc(JobSeekerProfile profile);

    List<SavedJob> findByJobSeekerProfileOrderBySavedAtDesc(JobSeekerProfile profile);
}
