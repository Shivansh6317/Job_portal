package com.example.auth.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJobGiverProfileRequest {

    private String fullName;
    private String jobTitle;
    private String companyName;
    private String location;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid contact number")
    private String contact;

    private String bio;
    private String companyDescription;

    @Pattern(regexp = "^https://.*linkedin\\.com/.*", message = "Invalid LinkedIn URL")
    private String linkedinProfileUrl;

    private List<String> specializations;
}
