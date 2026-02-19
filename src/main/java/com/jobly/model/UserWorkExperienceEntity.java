package com.jobly.model;

import com.jobly.gen.model.CVDataStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "USER_WORK_EXPERIENCE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWorkExperienceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "COMPANY_NAME")
    @Size(max = 255, message = "Company name must be at most 255 characters long")
    private String companyName;

    @Column(name = "DESIGNATION")
    @Size(max = 255, message = "Designation must be at most 255 characters long")
    private String designation;

    @Column(name = "START_DATE")
    private OffsetDateTime startDate;

    @Column(name = "END_DATE")
    private OffsetDateTime endDate;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private CVDataStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity user;
}
