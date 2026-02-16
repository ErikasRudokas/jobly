package com.jobly.model;

import com.jobly.gen.model.CVDataStatus;
import com.jobly.gen.model.SkillProficiency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_SKILL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PROFICIENCY_LEVEL")
    @Enumerated(EnumType.STRING)
    private SkillProficiency proficiencyLevel;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private CVDataStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SKILL_ID", nullable = false)
    private SkillEntity skill;
}
