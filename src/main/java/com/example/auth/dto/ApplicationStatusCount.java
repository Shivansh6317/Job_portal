package com.example.auth.dto;

import com.example.auth.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationStatusCount {
    private ApplicationStatus status;
    private long count;
}
