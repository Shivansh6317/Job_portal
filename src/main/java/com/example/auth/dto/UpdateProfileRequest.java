package com.example.auth.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

        private String firstName;
        private String lastName;

        @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid phone number")
        private String phoneNumber;

        private String bio;
        private List<String> languages;
        private List<String> skills;
}

