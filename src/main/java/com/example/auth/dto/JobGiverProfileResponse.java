
package com.example.auth.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobGiverProfileResponse {

    private Long id;
    private String fullName;
    private String jobTitle;
    private String companyName;
    private String location;
    private String contact;
    private String companyLogoUrl;
    private String bio;
    private String companyDescription;
    private String linkedinProfileUrl;
    private List<String> specializations;
    private Integer totalJobPosts;
}
