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
@Table(name = "USER_EDUCATION")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEducationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "INSTITUTION_NAME")
    @Size(max = 255, message = "Institution name must be at most 255 characters long")
    private String institutionName;

    @Column(name = "DEGREE")
    @Size(max = 255, message = "Degree must be at most 255 characters long")
    private String degree;

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
