package com.jobly.repository;

import com.jobly.enums.Role;
import com.jobly.gen.model.ApplicationStatus;
import com.jobly.gen.model.JobOfferStatus;
import com.jobly.gen.model.WorkType;
import com.jobly.model.ApplicationEntity;
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
class ApplicationRepositoryTests {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllByUserIdAndFilter_returnsMatchingApplications() {
        UserEntity applicant = persistUser("applicant@jobly.test", "applicant");
        UserEntity creator = persistUser("creator@jobly.test", "creator");
        JobOfferEntity jobOffer = persistJobOffer(creator, "Backend Developer", "Acme");

        ApplicationEntity pending = persistApplication(applicant, jobOffer, ApplicationStatus.PENDING);
        persistApplication(applicant, jobOffer, ApplicationStatus.REJECTED);

        entityManager.flush();

        List<ApplicationEntity> results = applicationRepository.findAllByUserIdAndFilter(
                applicant.getId(),
                List.of(ApplicationStatus.PENDING.getValue()),
                10,
                0
        );

        assertEquals(1, results.size());
        assertEquals(pending.getId(), results.get(0).getId());
    }

    @Test
    void countAllByUserIdAndFilter_countsMatchingApplications() {
        UserEntity applicant = persistUser("count@applicant.test", "count-applicant");
        UserEntity creator = persistUser("count@creator.test", "count-creator");
        JobOfferEntity jobOffer = persistJobOffer(creator, "Backend", "Acme");

        persistApplication(applicant, jobOffer, ApplicationStatus.PENDING);
        persistApplication(applicant, jobOffer, ApplicationStatus.PENDING);
        persistApplication(applicant, jobOffer, ApplicationStatus.REJECTED);

        entityManager.flush();

        Integer count = applicationRepository.countAllByUserIdAndFilter(
                applicant.getId(),
                List.of(ApplicationStatus.PENDING.getValue())
        );

        assertEquals(2, count);
    }

    @Test
    void findAllByJobOfferIdAndFilter_returnsMatchingApplications() {
        UserEntity applicant = persistUser("applicant2@jobly.test", "applicant2");
        UserEntity creator = persistUser("creator2@jobly.test", "creator2");
        JobOfferEntity jobOffer = persistJobOffer(creator, "Frontend Developer", "Beta");

        ApplicationEntity accepted = persistApplication(applicant, jobOffer, ApplicationStatus.ACCEPTED);
        persistApplication(applicant, jobOffer, ApplicationStatus.REJECTED);

        entityManager.flush();

        List<ApplicationEntity> results = applicationRepository.findAllByJobOfferIdAndFilter(
                jobOffer.getId(),
                List.of(ApplicationStatus.ACCEPTED.getValue()),
                10,
                0
        );

        assertEquals(1, results.size());
        assertEquals(accepted.getId(), results.get(0).getId());
    }

    @Test
    void countAllByJobOfferIdAndFilter_countsMatchingApplications() {
        UserEntity applicant = persistUser("applicant3@jobly.test", "applicant3");
        UserEntity creator = persistUser("creator3@jobly.test", "creator3");
        JobOfferEntity jobOffer = persistJobOffer(creator, "QA Engineer", "Gamma");

        persistApplication(applicant, jobOffer, ApplicationStatus.PENDING);
        persistApplication(applicant, jobOffer, ApplicationStatus.PENDING);
        persistApplication(applicant, jobOffer, ApplicationStatus.REJECTED);

        entityManager.flush();

        Integer count = applicationRepository.countAllByJobOfferIdAndFilter(
                jobOffer.getId(),
                List.of(ApplicationStatus.PENDING.getValue())
        );

        assertEquals(2, count);
    }

    private UserEntity persistUser(String email, String displayName) {
        UserEntity user = UserEntity.builder()
                .firstName("Test")
                .lastName("User")
                .displayName(displayName)
                .email(email)
                .passwordHash("hash")
                .role(Role.USER)
                .suspended(false)
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

    private ApplicationEntity persistApplication(UserEntity applicant, JobOfferEntity jobOffer, ApplicationStatus status) {
        ApplicationEntity application = ApplicationEntity.builder()
                .status(status)
                .comment("Comment")
                .jobOffer(jobOffer)
                .applicant(applicant)
                .build();
        entityManager.persist(application);
        return application;
    }
}
