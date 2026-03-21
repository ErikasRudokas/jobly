package com.jobly.repository;

import com.jobly.gen.model.SkillType;
import com.jobly.model.SkillAliasEntity;
import com.jobly.model.SkillEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class SkillRepositoryTests {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllSkillsByAliasSearch_matchesAliases() {
        SkillEntity java = persistSkill("Java");
        SkillEntity python = persistSkill("Python");

        persistAlias(java, "java");
        persistAlias(python, "python");

        entityManager.flush();

        List<SkillEntity> results = skillRepository.findAllSkillsByAliasSearch("jav", 10, 0);

        assertEquals(1, results.size());
        assertEquals(java.getId(), results.get(0).getId());
    }

    @Test
    void countAllSkillsByAliasSearch_countsDistinctSkills() {
        SkillEntity java = persistSkill("Java");
        persistAlias(java, "java");
        persistAlias(java, "java se");

        entityManager.flush();

        Integer count = skillRepository.countAllSkillsByAliasSearch("java");

        assertEquals(1, count);
    }

    private SkillEntity persistSkill(String name) {
        SkillEntity skill = SkillEntity.builder()
                .name(name)
                .description(name + " skill")
                .skillType(SkillType.TECHNICAL)
                .embedding(new Float[]{1.0F, 0.0F})
                .build();
        entityManager.persist(skill);
        return skill;
    }

    private SkillAliasEntity persistAlias(SkillEntity skill, String aliasValue) {
        SkillAliasEntity alias = SkillAliasEntity.builder()
                .alias(aliasValue)
                .skill(skill)
                .build();
        entityManager.persist(alias);
        return alias;
    }
}
