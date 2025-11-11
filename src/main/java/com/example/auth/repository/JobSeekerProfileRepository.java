
package com.example.auth.repository;

import com.example.auth.entity.JobSeekerProfile;
import com.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Long> {
    Optional<JobSeekerProfile> findByUser(User user);
    Optional<JobSeekerProfile> findByUserEmail(String email);
    boolean existsByUser(User user);
}
