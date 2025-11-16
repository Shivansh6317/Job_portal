package com.example.auth.repository;

import com.example.auth.entity.JobGiverProfile;
import com.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JobGiverProfileRepository extends JpaRepository<JobGiverProfile, Long> {
    Optional<JobGiverProfile> findByUser(User user);
    boolean existsByUser(User user);
}
