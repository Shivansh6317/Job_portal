package com.example.auth.dto;
import com.example.auth.entity.JobStatus;
import com.example.auth.entity.JobType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostResponse {

    private Long id;
    private String title;
    private String location;
    private JobType jobType;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String salaryCurrency;
    private String description;
    private String responsibilities;
    private String qualifications;
    private String experienceRequired;
    private List<String> skills;
    private JobStatus status;
    private LocalDateTime applicationDeadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Company info (from JobGiverProfile)
    private String companyName;
    private String companyLogoUrl;
    private String companyLocation;
}
