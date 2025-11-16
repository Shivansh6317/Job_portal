package com.example.auth.dto;

import com.example.auth.entity.ApplicationStatus;
import com.example.auth.entity.JobType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationResponse {

    private Long id;

    private Long jobPostId;
    private String jobTitle;
    private String companyName;
    private String companyLocation;
    private JobType jobType;

    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private String resumeUrl;
    private String additionalFileUrl;
}
