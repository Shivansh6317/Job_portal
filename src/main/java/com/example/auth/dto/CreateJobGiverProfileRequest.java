
package com.example.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobGiverProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Contact is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid contact number")
    private String contact;

    private String bio;

    private String companyDescription;

    @Pattern(regexp = "^https://.*linkedin\\.com/.*", message = "Invalid LinkedIn URL")
    private String linkedinProfileUrl;

    private List<String> specializations;
}


