package com.jobly.util;

import com.jobly.enums.CvStatus;
import com.jobly.enums.FileType;
import com.jobly.enums.TokenType;
import com.jobly.gen.model.*;
import com.jobly.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestEntityFactory {

    public static ApplicationEntity buildApplication(Long id, ApplicationStatus status, JobOfferEntity jobOffer) {
        return ApplicationEntity.builder()
                .id(id)
                .status(status)
                .comment("Initial comment")
                .updatedAt(OffsetDateTime.parse("2026-03-20T10:15:30Z"))
                .jobOffer(jobOffer)
                .build();
    }

    public static ApplicationEntity buildManageableApplication(Long id, Long creatorId, ApplicationStatus status) {
        JobOfferEntity jobOffer = buildJobOffer(400L + id, creatorId);
        return ApplicationEntity.builder()
                .id(id)
                .status(status)
                .jobOffer(jobOffer)
                .build();
    }

    public static JobOfferEntity buildJobOffer(Long jobOfferId, Long creatorId) {
        CategoryEntity category = buildCategory(77L, "Engineering");
        UserEntity creator = UserEntity.builder()
                .id(creatorId)
                .firstName("Alice")
                .lastName("Boss")
                .email("boss@jobly.test")
                .build();

        return JobOfferEntity.builder()
                .id(jobOfferId)
                .title("Backend Developer")
                .company("Acme")
                .salary(BigDecimal.valueOf(1000))
                .workType(WorkType.REMOTE)
                .location("Vilnius")
                .category(category)
                .creator(creator)
                .build();
    }

    public static UserEntity buildApplicant(Long applicantId) {
        return UserEntity.builder()
                .id(applicantId)
                .firstName("John")
                .lastName("Doe")
                .email("john@jobly.test")
                .build();
    }

    public static CategoryEntity buildCategory(Long id, String name) {
        return CategoryEntity.builder()
                .id(id)
                .name(name)
                .description("Category description")
                .build();
    }

    public static UserEntity buildUser(Long userId, String firstName, String lastName, String email, String displayName) {
        return UserEntity.builder()
                .id(userId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .displayName(displayName)
                .build();
    }

    public static UserEntity buildUserWithPassword(Long userId, String firstName, String lastName, String email, String displayName, String passwordHash) {
        return UserEntity.builder()
                .id(userId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .displayName(displayName)
                .passwordHash(passwordHash)
                .build();
    }

    public static UserCvEntity buildUserCv(Long cvId, UserEntity user, String title, byte[] fileData) {
        return UserCvEntity.builder()
                .id(cvId)
                .title(title)
                .fileType(FileType.PDF)
                .status(CvStatus.ACTIVE)
                .fileData(fileData)
                .user(user)
                .build();
    }

    public static SkillEntity buildSkill(Long id, String name, Float[] embedding) {
        return SkillEntity.builder()
                .id(id)
                .name(name)
                .description("Skill description")
                .skillType(SkillType.TECHNICAL)
                .embedding(embedding)
                .build();
    }

    public static JobSkillEntity buildJobSkill(Long id, JobOfferEntity jobOffer, SkillEntity skill, SkillProficiency proficiency) {
        return JobSkillEntity.builder()
                .id(id)
                .jobOffer(jobOffer)
                .skill(skill)
                .expectedProficiency(proficiency)
                .build();
    }

    public static UserSkillEntity buildUserSkill(Long id, UserEntity user, SkillEntity skill, SkillProficiency proficiency) {
        return UserSkillEntity.builder()
                .id(id)
                .user(user)
                .skill(skill)
                .proficiencyLevel(proficiency)
                .build();
    }

    public static UserSkillEntity buildUserSkillWithStatus(Long id, UserEntity user, SkillEntity skill, SkillProficiency proficiency, CVDataStatus status) {
        return UserSkillEntity.builder()
                .id(id)
                .user(user)
                .skill(skill)
                .proficiencyLevel(proficiency)
                .status(status)
                .build();
    }

    public static UserEducationEntity buildUserEducation(Long id, UserEntity user, String institution, String degree, CVDataStatus status) {
        return UserEducationEntity.builder()
                .id(id)
                .user(user)
                .institutionName(institution)
                .degree(degree)
                .status(status)
                .build();
    }

    public static UserWorkExperienceEntity buildUserWorkExperience(Long id, UserEntity user, String company, String designation, CVDataStatus status) {
        return UserWorkExperienceEntity.builder()
                .id(id)
                .user(user)
                .companyName(company)
                .designation(designation)
                .status(status)
                .build();
    }

    public static TokenEntity buildToken(Long tokenId, UserEntity user, String token, TokenType tokenType, boolean revoked) {
        return TokenEntity.builder()
                .id(tokenId)
                .user(user)
                .token(token)
                .tokenType(tokenType)
                .revoked(revoked)
                .build();
    }
}
