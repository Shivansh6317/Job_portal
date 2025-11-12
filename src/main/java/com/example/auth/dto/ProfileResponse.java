package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String bio;
        private String resumeUrl;
        private String profileImageUrl;
        private List<String> languages;
        private List<String> skills;
        private List<String> educations;
        private List<String> experiences;
    }

