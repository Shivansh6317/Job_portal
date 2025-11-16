package com.example.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class EmployerDashboardResponse {

    private long totalJobPosts;
    private long activeJobs;
    private long closedJobs;
    private long draftJobs;

    private long totalApplications;

    private List<ApplicationStatusCount> applicationStatusCounts;
    private List<JobApplicationStats> jobWiseStats;
}
