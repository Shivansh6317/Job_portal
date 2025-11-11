package com.example.auth.repository;

import com.example.auth.entity.JobGiverProfile;
import com.example.auth.entity.JobPost;
import com.example.auth.entity.JobStatus;
import com.example.auth.entity.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {


    List<JobPost> findByJobGiverProfile(JobGiverProfile profile);


    Page<JobPost> findByStatus(JobStatus status, Pageable pageable);


    @Query("SELECT j FROM JobPost j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND j.status = 'ACTIVE'")
    Page<JobPost> searchByTitle(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT j FROM JobPost j WHERE LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')) AND j.status = 'ACTIVE'")
    Page<JobPost> searchByLocation(@Param("location") String location, Pageable pageable);


    Page<JobPost> findByJobTypeAndStatus(JobType jobType, JobStatus status, Pageable pageable);

    @Query("SELECT j FROM JobPost j WHERE " +
            "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:jobType IS NULL OR j.jobType = :jobType) AND " +
            "(:minSalary IS NULL OR j.maxSalary >= :minSalary) AND " +
            "(:maxSalary IS NULL OR j.minSalary <= :maxSalary) AND " +
            "j.status = 'ACTIVE'")
    Page<JobPost> advancedSearch(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("jobType") JobType jobType,
            @Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            Pageable pageable
    );

    @Query("SELECT COUNT(j) FROM JobPost j WHERE j.jobGiverProfile = :profile AND j.status = 'ACTIVE'")
    Integer countActiveJobsByProfile(@Param("profile") JobGiverProfile profile);
}
