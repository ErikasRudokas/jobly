package com.jobly.service.validator;

import com.jobly.exception.general.BadRequestException;
import com.jobly.gen.model.SaveUserEducationRequest;
import com.jobly.gen.model.SaveUserProfileRequest;
import com.jobly.gen.model.SaveUserSkillRequest;
import com.jobly.gen.model.SaveUserWorkExperienceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class UserProfileValidator {

    public void validateUserProfileSaveRequest(SaveUserProfileRequest request) {
        validateSkills(request.getSkills());
        validateEducation(request.getEducation());
        validateWorkExperience(request.getWorkExperience());
    }

    private void validateSkills(List<SaveUserSkillRequest> skills) {
        skills.stream()
                .filter(skill -> !Boolean.TRUE.equals(skill.getDelete()))
                .forEach(skill -> {
                    if (skill.getProficiencyLevel() == null) {
                        throw new BadRequestException("Skill proficiency is required for skill");
                    }
                });
    }

    private void validateEducation(List<SaveUserEducationRequest> educations) {
        educations.stream()
                .filter(education -> !Boolean.TRUE.equals(education.getDelete()))
                .forEach(education -> {
                    if (education.getInstitutionName() == null || education.getInstitutionName().isBlank()) {
                        throw new BadRequestException("Institution name is required for education");
                    }
                    if (education.getDegree() == null || education.getDegree().isBlank()) {
                        throw new BadRequestException("Degree is required for education");
                    }
                    if (education.getStartDate() == null) {
                        throw new BadRequestException("Start date is required for education");
                    }
                    if (education.getEndDate() == null) {
                        throw new BadRequestException("End date is required for education");
                    }
                    if (education.getStartDate().isAfter(education.getEndDate())) {
                        throw new BadRequestException("Start date cannot be after end date for education");
                    }
                });
    }

    private void validateWorkExperience(List<SaveUserWorkExperienceRequest> workExperiences) {
        workExperiences.stream()
                .filter(workExperience -> !Boolean.TRUE.equals(workExperience.getDelete()))
                .forEach(workExperience -> {
                    if (workExperience.getCompanyName() == null || workExperience.getCompanyName().isBlank()) {
                        throw new BadRequestException("Company name is required for work experience");
                    }
                    if (workExperience.getDesignation() == null || workExperience.getDesignation().isBlank()) {
                        throw new BadRequestException("Designation is required for work experience");
                    }
                    if (workExperience.getStartDate() == null) {
                        throw new BadRequestException("Start date is required for work experience");
                    }
                    if (workExperience.getEndDate() == null) {
                        throw new BadRequestException("End date is required for work experience");
                    }
                    if (workExperience.getStartDate().isAfter(workExperience.getEndDate())) {
                        throw new BadRequestException("Start date cannot be after end date for work experience");
                    }
                });
    }
}
