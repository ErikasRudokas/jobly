package com.jobly.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SKILL_ALIASES")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillAliasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ALIAS", nullable = false)
    @Size(max = 64, message = "Skill alias must be at most 64 characters long")
    private String alias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SKILL_ID", nullable = false)
    private SkillEntity skill;
}
