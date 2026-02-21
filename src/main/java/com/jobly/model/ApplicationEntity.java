package com.jobly.model;

import com.jobly.gen.model.ApplicationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "APPLICATIONS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "COMMENT")
    @Size(max = 1000, message = "Comment must be at most 1000 characters long")
    private String comment;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_OFFER_ID", nullable = false)
    private JobOfferEntity jobOffer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICANT_ID", nullable = false)
    private UserEntity applicant;
}
