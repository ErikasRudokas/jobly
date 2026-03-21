package com.jobly.repository;

import com.jobly.enums.Role;
import com.jobly.gen.model.JobOfferStatus;
import com.jobly.gen.model.SkillProficiency;
import com.jobly.gen.model.SkillType;
import com.jobly.gen.model.WorkType;
import com.jobly.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class JobSkillRepositoryTests {

    @Autowired
    private JobSkillRepository jobSkillRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deleteAllBySkillIdInAndJobOfferId_removesMatchingSkills() {
        UserEntity creator = persistUser("creator@jobly.test", "creator");
        CategoryEntity category = persistCategory();
        JobOfferEntity jobOffer = persistJobOffer(creator, category);

        SkillEntity javaSkill = persistSkill("Java");
        SkillEntity sqlSkill = persistSkill("SQL");
        SkillEntity dockerSkill = persistSkill("Docker");

        JobSkillEntity javaJobSkill = persistJobSkill(jobOffer, javaSkill);
        JobSkillEntity sqlJobSkill = persistJobSkill(jobOffer, sqlSkill);
        JobSkillEntity dockerJobSkill = persistJobSkill(jobOffer, dockerSkill);

        entityManager.flush();

        jobSkillRepository.deleteAllBySkillIdInAndJobOfferId(List.of(sqlSkill.getId(), dockerSkill.getId()), jobOffer.getId());
        entityManager.flush();

        List<JobSkillEntity> remaining = jobSkillRepository.findByJobOfferId(jobOffer.getId());

        assertEquals(1, remaining.size());
        assertEquals(javaJobSkill.getId(), remaining.get(0).getId());
    }

    private UserEntity persistUser(String email, String displayName) {
        UserEntity user = UserEntity.builder()
                .firstName("Test")
                .lastName("User")
                .displayName(displayName)
                .email(email)
                .passwordHash("hash")
                .role(Role.EMPLOYER)
                .build();
        entityManager.persist(user);
        return user;
    }

    private CategoryEntity persistCategory() {
        CategoryEntity category = CategoryEntity.builder()
                .name("Engineering")
                .description("Engineering roles")
                .build();
        entityManager.persist(category);
        return category;
    }

    private JobOfferEntity persistJobOffer(UserEntity creator, CategoryEntity category) {
        JobOfferEntity jobOffer = JobOfferEntity.builder()
                .title("Backend")
                .description("Description")
                .company("Acme")
                .salary(BigDecimal.valueOf(1200))
                .yearsOfExperience(2)
                .workType(WorkType.REMOTE)
                .location("Vilnius")
                .contactEmail("hr@acme.test")
                .contactPhone("123")
                .status(JobOfferStatus.OPEN)
                .category(category)
                .creator(creator)
                .build();
        entityManager.persist(jobOffer);
        return jobOffer;
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

    private JobSkillEntity persistJobSkill(JobOfferEntity jobOffer, SkillEntity skill) {
        JobSkillEntity jobSkill = JobSkillEntity.builder()
                .jobOffer(jobOffer)
                .skill(skill)
                .expectedProficiency(SkillProficiency.BEGINNER)
                .build();
        entityManager.persist(jobSkill);
        return jobSkill;
    }
}
