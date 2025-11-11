package com.example.auth.dto;
import com.example.auth.entity.JobStatus;
import com.example.auth.entity.JobType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostSummary {

    private Long id;
    private String title;
    private String companyName;
    private String location;
    private JobType jobType;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String salaryCurrency;
    private String experienceRequired;
    private JobStatus status;
    private LocalDateTime createdAt;
}
