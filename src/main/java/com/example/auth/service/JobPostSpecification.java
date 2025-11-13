package com.example.auth.service;

import com.example.auth.entity.JobPost;
import com.example.auth.entity.JobStatus;
import com.example.auth.entity.JobType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class JobPostSpecification {

    public static Specification<JobPost> keyword(String keyword) {
        return (root, query, cb) ->
                keyword == null || keyword.trim().isEmpty()
                        ? null
                        : cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<JobPost> hasLocation(String location) {
        return (root, query, cb) ->
                location == null || location.trim().isEmpty()
                        ? null
                        : cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<JobPost> hasJobType(JobType jobType) {
        return (root, query, cb) ->
                jobType == null ? null : cb.equal(root.get("jobType"), jobType);
    }

    public static Specification<JobPost> minSalary(BigDecimal minSalary) {
        return (root, query, cb) ->
                minSalary == null ? null : cb.greaterThanOrEqualTo(root.get("maxSalary"), minSalary);
    }

    public static Specification<JobPost> maxSalary(BigDecimal maxSalary) {
        return (root, query, cb) ->
                maxSalary == null ? null : cb.lessThanOrEqualTo(root.get("minSalary"), maxSalary);
    }

    public static Specification<JobPost> hasSkill(String skill) {
        return (root, query, cb) ->
                skill == null || skill.trim().isEmpty()
                        ? null
                        : cb.isMember(skill, root.get("skills"));
    }

    public static Specification<JobPost> hasStatus(JobStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }
    public static Specification<JobPost> hasCompanyName(String companyName) {
        return (root, query, cb) -> {
            if (companyName == null || companyName.trim().isEmpty()) return null;

            return cb.like(
                    cb.lower(root.get("jobGiverProfile").get("companyName")),
                    "%" + companyName.toLowerCase() + "%"
            );
        };
    }
}
