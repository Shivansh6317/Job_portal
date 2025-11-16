package com.example.auth.dto;

import com.example.auth.entity.JobStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobApplicationStats {

    private Long jobPostId;
    private String title;
    private JobStatus status;
    private long applicants;
}
