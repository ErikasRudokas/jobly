package com.jobly.model;

import com.jobly.gen.model.AdminUserActionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ADMIN_USER_ACTIONS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminUserActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ACTION", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminUserActionType action;

    @Column(name = "COMMENT", nullable = false)
    @Size(max = 1000, message = "Comment must be at most 1000 characters long")
    private String comment;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_USER_ID", nullable = false)
    private UserEntity targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_USER_ID", nullable = false)
    private UserEntity performedBy;
}

