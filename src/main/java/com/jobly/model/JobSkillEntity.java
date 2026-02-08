package com.jobly.model;

import com.jobly.gen.model.SkillProficiency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "JOB_SKILLS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobSkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EXPECTED_PROFICIENCY", nullable = false)
    @Enumerated(EnumType.STRING)
    private SkillProficiency expectedProficiency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SKILL_ID", nullable = false)
    private SkillEntity skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_OFFER_ID", nullable = false)
    private JobOfferEntity jobOffer;
}
