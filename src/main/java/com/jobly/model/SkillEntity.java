package com.jobly.model;

import com.jobly.gen.model.SkillType;
import com.jobly.model.converter.SkillEmbeddingConverter;
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

    @Column(name = "DESCRIPTION", nullable = false)
    @Size(max = 255, message = "Skill description must be at most 255 characters long")
    private String description;

    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private SkillType skillType;

    @Convert(converter = SkillEmbeddingConverter.class)
    @Column(columnDefinition = "TEXT")
    private Float[] embedding;
}
