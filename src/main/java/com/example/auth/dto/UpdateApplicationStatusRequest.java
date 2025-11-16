package com.example.auth.dto;

import com.example.auth.entity.ApplicationStatus;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {
    private ApplicationStatus status;
}
