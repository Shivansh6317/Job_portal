package com.example.auth.dto;

import com.example.auth.dto.JobPostSummary;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class JobSeekerDashboardResponse {

    private long totalApplications;
    private Map<String, Long> applicationStatusBreakdown;

    private long savedJobs;

    private List<JobPostSummary> recentAppliedJobs;
    private List<JobPostSummary> recentSavedJobs;
}
