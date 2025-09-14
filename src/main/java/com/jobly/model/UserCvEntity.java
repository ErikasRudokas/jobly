package com.jobly.model;

import com.jobly.enums.CvStatus;
import com.jobly.enums.FileType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "USER_CV")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCvEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE", nullable = false)
    @Size(max = 32, message = "Title must be at most 32 characters long")
    private String title;

    @Column(name = "FILE_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Column(name = "FILE_DATA", nullable = false, columnDefinition = "BYTEA")
    private byte[] fileData;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private CvStatus status;

    @Column(name = "UPLOADED_AT", nullable = false)
    @CreationTimestamp
    private OffsetDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity user;
}
