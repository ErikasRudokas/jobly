package com.jobly.service;

import com.jobly.dao.*;
import com.jobly.exception.general.BadRequestException;
import com.jobly.exception.general.ForbiddenException;
import com.jobly.gen.model.*;
import com.jobly.mapper.UserEducationMapper;
import com.jobly.mapper.UserSkillMapper;
import com.jobly.mapper.UserWorkExperienceMapper;
import com.jobly.model.*;
import com.jobly.service.validator.UserProfileValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserSkillDao userSkillDao;
    private final UserEducationDao userEducationDao;
    private final UserWorkExperienceDao userWorkExperienceDao;
    private final UserDao userDao;
    private final SkillDao skillDao;
    private final UserProfileValidator userProfileValidator;

    @PreAuthorize("hasRole('USER')")
    public GetUserProfileResponse getUserProfile(Long userId) {
        List<UserSkillEntity> userSkills = userSkillDao.findAllByUserId(userId);
        List<UserEducationEntity> userEducation = userEducationDao.findAllByUserId(userId);
        List<UserWorkExperienceEntity> userWorkExperience = userWorkExperienceDao.findAllByUserId(userId);

        GetUserProfileResponse response = new GetUserProfileResponse();
        response.setSkills(getUserSkillResponse(userSkills));
        response.setEducation(getUserEducationResponse(userEducation));
        response.setWorkExperience(getUserWorkExperienceResponse(userWorkExperience));
        return response;
    }

    private static List<UserSkill> getUserSkillResponse(List<UserSkillEntity> userSkills) {
        return userSkills.stream()
                .map(UserSkillMapper::fromEntityToResponse)
                .toList();
    }

    private static List<UserEducation> getUserEducationResponse(List<UserEducationEntity> userEducation) {
        return userEducation.stream()
                .map(UserEducationMapper::fromEntityToResponse)
                .toList();
    }

    private static List<UserWorkExperience> getUserWorkExperienceResponse(List<UserWorkExperienceEntity> userWorkExperience) {
        return userWorkExperience.stream()
                .map(UserWorkExperienceMapper::fromEntityToResponse)
                .toList();
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public GetUserProfileResponse saveUserProfile(Long userId, SaveUserProfileRequest saveUserProfileRequest) {
        UserEntity userEntity = userDao.findById(userId);

        userProfileValidator.validateUserProfileSaveRequest(saveUserProfileRequest);

        var savedSkillEntities = processUserSkills(saveUserProfileRequest, userEntity);
        var savedEducationEntities = processUserEducation(saveUserProfileRequest, userEntity);
        var savedWorkExperienceEntities = processUserWorkExperience(saveUserProfileRequest, userEntity);

        GetUserProfileResponse response = new GetUserProfileResponse();
        response.setSkills(getUserSkillResponse(savedSkillEntities));
        response.setEducation(getUserEducationResponse(savedEducationEntities));
        response.setWorkExperience(getUserWorkExperienceResponse(savedWorkExperienceEntities));
        return response;
    }

    private List<UserSkillEntity> processUserSkills(SaveUserProfileRequest saveUserProfileRequest, UserEntity userEntity) {
        List<UserSkillEntity> existingUserSkills = userSkillDao.findAllByUserId(userEntity.getId());
        Map<Long, UserSkillEntity> existingSkillsMap = convertUserSkillsToMap(existingUserSkills);

        deleteUserSkills(saveUserProfileRequest, existingSkillsMap);
        return persistUserSkills(saveUserProfileRequest, userEntity, existingSkillsMap);
    }

    private Map<Long, UserSkillEntity> convertUserSkillsToMap(List<UserSkillEntity> existingUserSkills) {
        return existingUserSkills.stream()
                .collect(
                        Collectors.toMap(
                                UserSkillEntity::getId,
                                Function.identity())
                );
    }

    private void deleteUserSkills(SaveUserProfileRequest saveUserProfileRequest, Map<Long, UserSkillEntity> existingSkillsMap) {
        List<UserSkillEntity> userSkillsToDelete = getUserSkillsToDelete(saveUserProfileRequest, existingSkillsMap);
        userSkillDao.deleteAll(userSkillsToDelete);
    }

    private List<UserSkillEntity> getUserSkillsToDelete(SaveUserProfileRequest saveUserProfileRequest, Map<Long, UserSkillEntity> existingSkillsMap) {
        return saveUserProfileRequest.getSkills().stream()
                .filter(skill -> Boolean.TRUE.equals(skill.getDelete()))
                .map(skill -> existingSkillsMap.get(skill.getId()))
                .filter(Objects::nonNull)
                .toList();
    }

    private List<UserSkillEntity> persistUserSkills(SaveUserProfileRequest saveUserProfileRequest,
                                                    UserEntity userEntity,
                                                    Map<Long, UserSkillEntity> existingSkillsMap) {
        log.info("Persisting user skills for user {}", userEntity.getId());
        List<Long> skillIdsToFetch = getUserSkillsNotForDeletion(saveUserProfileRequest);
        Map<Long, SkillEntity> skillEntityMap = converToSkillEntityMap(skillDao.findAllByIds(skillIdsToFetch));

        List<UserSkillEntity> skillsToSave = saveUserProfileRequest.getSkills().stream()
                .filter(s -> !Boolean.TRUE.equals(s.getDelete()))
                .map(skillRequest -> {
                    if (skillRequest.getId() == null) {
                        SkillEntity matchingSkillEntity = getMatchingSkillEntity(skillRequest, skillEntityMap);
                        return UserSkillMapper.fromSaveRequestToEntity(skillRequest, userEntity, matchingSkillEntity);
                    }

                    UserSkillEntity existingUserSkill = getExistingUserSkill(skillRequest, existingSkillsMap);
                    UserSkillMapper.updateEntity(existingUserSkill, skillRequest);
                    return existingUserSkill;
                })
                .toList();
        return userSkillDao.saveAll(skillsToSave);
    }

    private List<Long> getUserSkillsNotForDeletion(SaveUserProfileRequest saveUserProfileRequest) {
        return saveUserProfileRequest.getSkills().stream()
                .filter(skill -> !Boolean.TRUE.equals(skill.getDelete()))
                .map(SaveUserSkillRequest::getSkillId)
                .toList();
    }

    private Map<Long, SkillEntity> converToSkillEntityMap(Set<SkillEntity> skillEntities) {
        return skillEntities.stream()
                .collect(Collectors.toMap(SkillEntity::getId, Function.identity()));
    }

    private UserSkillEntity getExistingUserSkill(SaveUserSkillRequest skillRequest, Map<Long, UserSkillEntity> existingSkillsMap) {
        return Optional.ofNullable(existingSkillsMap.get(skillRequest.getId()))
                .orElseThrow(() -> new ForbiddenException("Skill does not belong to user"));
    }

    private SkillEntity getMatchingSkillEntity(SaveUserSkillRequest skillRequest, Map<Long, SkillEntity> skillEntityMap) {
        return Optional.ofNullable(skillEntityMap.get(skillRequest.getSkillId()))
                .orElseThrow(() -> new BadRequestException("Skill not found"));
    }


    private List<UserEducationEntity> processUserEducation(SaveUserProfileRequest saveUserProfileRequest, UserEntity userEntity) {
        List<UserEducationEntity> existingUserEducations = userEducationDao.findAllByUserId(userEntity.getId());
        Map<Long, UserEducationEntity> existingEducationMap = convertUserEducationToMap(existingUserEducations);

        deleteUserEducation(saveUserProfileRequest, existingEducationMap);
        return persistUserEducation(saveUserProfileRequest, userEntity, existingEducationMap);
    }

    private static Map<Long, UserEducationEntity> convertUserEducationToMap(List<UserEducationEntity> existingUserEducations) {
        return existingUserEducations.stream()
                .collect(
                        Collectors.toMap(
                                UserEducationEntity::getId,
                                Function.identity())
                );
    }

    private void deleteUserEducation(SaveUserProfileRequest saveUserProfileRequest, Map<Long, UserEducationEntity> existingEducationMap) {
        List<UserEducationEntity> educationsToDelete = getUserEducationToDelete(saveUserProfileRequest, existingEducationMap);
        userEducationDao.deleteAll(educationsToDelete);
    }

    private static List<UserEducationEntity> getUserEducationToDelete(SaveUserProfileRequest saveUserProfileRequest,
                                                                      Map<Long, UserEducationEntity> existingEducationMap) {
        return saveUserProfileRequest.getEducation().stream()
                .filter(education -> Boolean.TRUE.equals(education.getDelete()))
                .map(education -> existingEducationMap.get(education.getId()))
                .filter(Objects::nonNull)
                .toList();
    }

    private List<UserEducationEntity> persistUserEducation(SaveUserProfileRequest saveUserProfileRequest,
                                                           UserEntity userEntity,
                                                           Map<Long, UserEducationEntity> existingEducationMap) {
        log.info("Persisting user education for user {}", userEntity.getId());
        var educationEntitiesToSave = saveUserProfileRequest.getEducation().stream()
                .filter(education -> !Boolean.TRUE.equals(education.getDelete()))
                .map(education -> {
                    if (education.getId() == null) {
                        return UserEducationMapper.fromSaveRequestToEntity(education, userEntity);
                    }

                    UserEducationEntity existingUserEducation = getExistingUserEducation(existingEducationMap, education);
                    UserEducationMapper.updateEntity(existingUserEducation, education);
                    return existingUserEducation;
                })
                .toList();

        return userEducationDao.saveAll(educationEntitiesToSave);
    }

    private static UserEducationEntity getExistingUserEducation(Map<Long, UserEducationEntity> existingEducationMap, SaveUserEducationRequest education) {
        return Optional.ofNullable(existingEducationMap.get(education.getId()))
                .orElseThrow(() -> new ForbiddenException("Education does not belong to user"));
    }

    private List<UserWorkExperienceEntity> processUserWorkExperience(SaveUserProfileRequest saveUserProfileRequest, UserEntity userEntity) {
        List<UserWorkExperienceEntity> existingUserWorkExperience = userWorkExperienceDao.findAllByUserId(userEntity.getId());
        Map<Long, UserWorkExperienceEntity> existingWorkExperienceMap = convertUserWorkExperienceToMap(existingUserWorkExperience);

        deleteUserWorkExperience(saveUserProfileRequest, existingWorkExperienceMap);
        return persistUserWorkExperience(saveUserProfileRequest, userEntity, existingWorkExperienceMap);
    }

    private static Map<Long, UserWorkExperienceEntity> convertUserWorkExperienceToMap(List<UserWorkExperienceEntity> existingUserWorkExperience) {
        return existingUserWorkExperience.stream()
                .collect(Collectors.toMap(UserWorkExperienceEntity::getId, Function.identity()));
    }

    private void deleteUserWorkExperience(SaveUserProfileRequest saveUserProfileRequest, Map<Long, UserWorkExperienceEntity> existingWorkExperienceMap) {
        List<UserWorkExperienceEntity> workExperienceToDelete = getUserWorkExperienceToDelete(saveUserProfileRequest, existingWorkExperienceMap);
        userWorkExperienceDao.deleteAll(workExperienceToDelete);
    }

    private static List<UserWorkExperienceEntity> getUserWorkExperienceToDelete(SaveUserProfileRequest saveUserProfileRequest,
                                                                                Map<Long, UserWorkExperienceEntity> existingWorkExperienceMap) {
        return saveUserProfileRequest.getWorkExperience().stream()
                .filter(workExperience -> Boolean.TRUE.equals(workExperience.getDelete()))
                .map(workExperience -> existingWorkExperienceMap.get(workExperience.getId()))
                .filter(Objects::nonNull)
                .toList();
    }

    private List<UserWorkExperienceEntity> persistUserWorkExperience(SaveUserProfileRequest saveUserProfileRequest, UserEntity userEntity,
                                                                     Map<Long, UserWorkExperienceEntity> existingWorkExperienceMap) {
        log.info("Persisting user work experience for user {}", userEntity.getId());
        var workExperienceEntitiesToSave = saveUserProfileRequest.getWorkExperience().stream()
                .filter(workExperience -> !Boolean.TRUE.equals(workExperience.getDelete()))
                .map(workExperience -> {
                    if (workExperience.getId() == null) {
                        return UserWorkExperienceMapper.fromSaveRequestToEntity(workExperience, userEntity);
                    }

                    UserWorkExperienceEntity existing = getMatchingUserWorkExperience(existingWorkExperienceMap, workExperience);
                    UserWorkExperienceMapper.updateEntity(existing, workExperience);
                    return existing;
                })
                .toList();

        return userWorkExperienceDao.saveAll(workExperienceEntitiesToSave);
    }

    private static UserWorkExperienceEntity getMatchingUserWorkExperience(Map<Long, UserWorkExperienceEntity> existingWorkExperienceMap,
                                                                          SaveUserWorkExperienceRequest workExperience) {
        return Optional.ofNullable(existingWorkExperienceMap.get(workExperience.getId()))
                .orElseThrow(() -> new ForbiddenException("Work experience does not belong to user"));
    }
}
