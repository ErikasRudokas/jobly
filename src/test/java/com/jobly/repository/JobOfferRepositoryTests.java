package com.jobly.repository;

import com.jobly.enums.Role;
import com.jobly.gen.model.JobOfferStatus;
import com.jobly.gen.model.WorkType;
import com.jobly.model.CategoryEntity;
import com.jobly.model.JobOfferEntity;
import com.jobly.model.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class JobOfferRepositoryTests {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllWithFilter_matchesTitleOrCompany() {
        UserEntity creator = persistUser("creator@jobly.test", "creator");
        persistJobOffer(creator, "Backend Developer", "Acme");
        persistJobOffer(creator, "Designer", "Studio");

        entityManager.flush();

        List<JobOfferEntity> results = jobOfferRepository.findAllWithFilter("backend", 10, 0);

        assertEquals(1, results.size());
        assertEquals("Backend Developer", results.get(0).getTitle());
    }

    @Test
    void countAllWithFilter_countsMatches() {
        UserEntity creator = persistUser("creator2@jobly.test", "creator2");
        persistJobOffer(creator, "Backend Developer", "Acme");
        persistJobOffer(creator, "Backend Engineer", "Beta");

        entityManager.flush();

        Integer count = jobOfferRepository.countAllWithFilter("backend");

        assertEquals(2, count);
    }

    @Test
    void findAllByUserIdWithFilter_filtersByCreatorAndSearch() {
        UserEntity creator = persistUser("creator3@jobly.test", "creator3");
        UserEntity other = persistUser("other@jobly.test", "other");

        persistJobOffer(creator, "Backend Developer", "Acme");
        persistJobOffer(other, "Backend Developer", "OtherCo");

        entityManager.flush();

        List<JobOfferEntity> results = jobOfferRepository.findAllByUserIdWithFilter(creator.getId(), "backend", 10, 0);

        assertEquals(1, results.size());
        assertEquals(creator.getId(), results.get(0).getCreator().getId());
    }

    @Test
    void countAllByUserIdWithFilter_countsCreatorMatches() {
        UserEntity creator = persistUser("creator4@jobly.test", "creator4");
        persistJobOffer(creator, "Backend Developer", "Acme");
        persistJobOffer(creator, "Backend Engineer", "Beta");

        entityManager.flush();

        Integer count = jobOfferRepository.countAllByUserIdWithFilter(creator.getId(), "backend");

        assertEquals(2, count);
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

    private JobOfferEntity persistJobOffer(UserEntity creator, String title, String company) {
        CategoryEntity category = CategoryEntity.builder()
                .name("Engineering")
                .description("Engineering roles")
                .build();
        entityManager.persist(category);

        JobOfferEntity jobOffer = JobOfferEntity.builder()
                .title(title)
                .description("Description")
                .company(company)
                .salary(BigDecimal.valueOf(1200))
                .yearsOfExperience(2)
                .workType(WorkType.REMOTE)
                .location("Vilnius")
                .contactEmail("hr@" + company.toLowerCase() + ".test")
                .contactPhone("123")
                .status(JobOfferStatus.OPEN)
                .category(category)
                .creator(creator)
                .build();
        entityManager.persist(jobOffer);
        return jobOffer;
    }
}
