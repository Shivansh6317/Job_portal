package com.example.auth.dto;
import com.example.auth.entity.JobType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobPostRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    @DecimalMin(value = "0.0", message = "Minimum salary must be positive")
    private BigDecimal minSalary;

    @DecimalMin(value = "0.0", message = "Maximum salary must be positive")
    private BigDecimal maxSalary;

    private String salaryCurrency;

    @NotBlank(message = "Description is required")
    @Size(min = 50, message = "Description must be at least 50 characters")
    private String description;

    private String responsibilities;

    private String qualifications;

    private String experienceRequired;

    private List<String> skills;

    private LocalDateTime applicationDeadline;
}
