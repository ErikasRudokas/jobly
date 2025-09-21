package com.jobly.model;

import com.jobly.gen.model.JobOfferStatus;
import com.jobly.gen.model.WorkType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "JOB_OFFERS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobOfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE", nullable = false)
    @Size(max = 32, message = "Title must be at most 32 characters long")
    private String title;

    @Column(name = "DESCRIPTION", nullable = false, length = 1024)
    @Size(max = 1024, message = "Description must be at most 1024 characters long")
    private String description;

    @Column(name = "COMPANY", nullable = false)
    @Size(max = 60, message = "Company name must be at most 60 characters long")
    private String company;

    @Column(name = "SALARY", nullable = false, precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(name = "YEARS_OF_EXPERIENCE", nullable = false)
    private Integer yearsOfExperience;

    @Column(name = "WORK_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkType workType;

    @Column(name = "LOCATION")
    @Size(max = 100, message = "Location must be at most 100 characters long")
    private String location;

    @Column(name = "CONTACT_EMAIL", nullable = false)
    @Size(max = 100, message = "Contact email must be at most 100 characters long")
    @Email
    private String contactEmail;

    @Column(name = "CONTACT_PHONE", nullable = false)
    @Size(max = 15, message = "Contact phone must be at most 15 characters long")
    private String contactPhone;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobOfferStatus status;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATOR_ID", nullable = false)
    private UserEntity creator;
}
