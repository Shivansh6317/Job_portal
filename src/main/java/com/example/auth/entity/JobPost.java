package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_giver_profile_id", nullable = false)
    @ToString.Exclude
    private JobGiverProfile jobGiverProfile;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Column(name = "min_salary")
    private BigDecimal minSalary;

    @Column(name = "max_salary")
    private BigDecimal maxSalary;

    @Column(name = "salary_currency", length = 3)
    @Builder.Default
    private String salaryCurrency = "USD"; // USD, INR, EUR, etc.

    @Column(length = 3000, nullable = false)
    private String description;

    @Column(length = 2000)
    private String responsibilities;

    @Column(length = 2000)
    private String qualifications;

    @Column(name = "experience_required")
    private String experienceRequired; // e.g., "2-5 years", "Entry Level"

    @ElementCollection
    @CollectionTable(name = "job_post_skills", joinColumns = @JoinColumn(name = "job_post_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JobStatus status = JobStatus.ACTIVE; // ACTIVE, CLOSED, DRAFT

    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;

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