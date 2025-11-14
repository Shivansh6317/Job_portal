package com.example.auth.repository;

import com.example.auth.entity.JobGiverProfile;
import com.example.auth.entity.JobPost;
import com.example.auth.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor<JobPost> {
    List<JobPost> findByJobGiverProfile(JobGiverProfile profile);

}
