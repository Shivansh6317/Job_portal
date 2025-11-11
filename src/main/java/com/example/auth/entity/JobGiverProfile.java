package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_giver_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobGiverProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String contact;

    @Column(name = "company_logo_url")
    private String companyLogoUrl;

    @Column(length = 2000)
    private String bio;

    @Column(length = 2000)
    private String companyDescription;

    @Column(name = "linkedin_profile_url")
    private String linkedinProfileUrl;

    @ElementCollection
    @CollectionTable(name = "profile_specializations", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "specialization")
    @Builder.Default
    private List<String> specializations = new ArrayList<>();

    @OneToMany(mappedBy = "jobGiverProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JobPost> jobPosts = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
