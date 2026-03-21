package com.jobly.service.validator;

import com.jobly.exception.general.BadRequestException;
import com.jobly.gen.model.*;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserProfileValidatorTests {

    private final UserProfileValidator validator = new UserProfileValidator();

    @Test
    void validateUserProfileSaveRequest_missingSkillProficiencyThrows() {
        SaveUserSkillRequest skill = new SaveUserSkillRequest()
                .skillId(1L);

        SaveUserProfileRequest request = new SaveUserProfileRequest()
                .skills(List.of(skill))
                .education(List.of())
                .workExperience(List.of());

        assertThrows(BadRequestException.class, () -> validator.validateUserProfileSaveRequest(request));
    }

    @Test
    void validateUserProfileSaveRequest_missingEducationFieldsThrow() {
        SaveUserEducationRequest education = new SaveUserEducationRequest()
                .degree("CS")
                .startDate(OffsetDateTime.parse("2020-01-01T00:00:00Z"));

        SaveUserProfileRequest request = new SaveUserProfileRequest()
                .skills(List.of())
                .education(List.of(education))
                .workExperience(List.of());

        assertThrows(BadRequestException.class, () -> validator.validateUserProfileSaveRequest(request));
    }

    @Test
    void validateUserProfileSaveRequest_educationDatesOutOfOrderThrow() {
        SaveUserEducationRequest education = new SaveUserEducationRequest()
                .institutionName("Tech University")
                .degree("CS")
                .startDate(OffsetDateTime.parse("2021-01-01T00:00:00Z"))
                .endDate(OffsetDateTime.parse("2020-01-01T00:00:00Z"));

        SaveUserProfileRequest request = new SaveUserProfileRequest()
                .skills(List.of())
                .education(List.of(education))
                .workExperience(List.of());

        assertThrows(BadRequestException.class, () -> validator.validateUserProfileSaveRequest(request));
    }

    @Test
    void validateUserProfileSaveRequest_missingWorkExperienceFieldsThrow() {
        SaveUserWorkExperienceRequest work = new SaveUserWorkExperienceRequest()
                .designation("Engineer")
                .startDate(OffsetDateTime.parse("2020-01-01T00:00:00Z"));

        SaveUserProfileRequest request = new SaveUserProfileRequest()
                .skills(List.of())
                .education(List.of())
                .workExperience(List.of(work));

        assertThrows(BadRequestException.class, () -> validator.validateUserProfileSaveRequest(request));
    }

    @Test
    void validateUserProfileSaveRequest_workExperienceDatesOutOfOrderThrow() {
        SaveUserWorkExperienceRequest work = new SaveUserWorkExperienceRequest()
                .companyName("Acme")
                .designation("Engineer")
                .startDate(OffsetDateTime.parse("2021-01-01T00:00:00Z"))
                .endDate(OffsetDateTime.parse("2020-01-01T00:00:00Z"));

        SaveUserProfileRequest request = new SaveUserProfileRequest()
                .skills(List.of())
                .education(List.of())
                .workExperience(List.of(work));

        assertThrows(BadRequestException.class, () -> validator.validateUserProfileSaveRequest(request));
    }

    @Test
    void validateUserProfileSaveRequest_validRequestDoesNotThrow() {
        SaveUserSkillRequest skill = new SaveUserSkillRequest()
                .skillId(1L)
                .proficiencyLevel(SkillProficiency.INTERMEDIATE);

        SaveUserEducationRequest education = new SaveUserEducationRequest()
                .institutionName("Tech University")
                .degree("CS")
                .startDate(OffsetDateTime.parse("2020-01-01T00:00:00Z"))
                .endDate(OffsetDateTime.parse("2021-01-01T00:00:00Z"));

        SaveUserWorkExperienceRequest work = new SaveUserWorkExperienceRequest()
                .companyName("Acme")
                .designation("Engineer")
                .startDate(OffsetDateTime.parse("2020-01-01T00:00:00Z"))
                .endDate(OffsetDateTime.parse("2021-01-01T00:00:00Z"));

        SaveUserProfileRequest request = new SaveUserProfileRequest()
                .skills(List.of(skill))
                .education(List.of(education))
                .workExperience(List.of(work));

        assertDoesNotThrow(() -> validator.validateUserProfileSaveRequest(request));
    }
}

