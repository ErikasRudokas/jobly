package com.jobly.model;

import com.jobly.gen.model.SkillType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SKILLS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false)
    @Size(max = 64, message = "Skill name must be at most 64 characters long")
    private String name;

    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private SkillType skillType;
}
